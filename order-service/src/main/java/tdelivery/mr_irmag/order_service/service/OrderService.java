package tdelivery.mr_irmag.order_service.service;

import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import tdelivery.mr_irmag.order_service.exception.InvalidOrderStatusException;
import tdelivery.mr_irmag.order_service.kafka.KafkaProducerService;
import tdelivery.mr_irmag.order_service.domain.dto.KafkaDelayedMessageDTO;
import tdelivery.mr_irmag.order_service.domain.dto.ProcessCourierOrderRequest;
import tdelivery.mr_irmag.order_service.domain.dto.calculationDelivery.CalculateOrderRequest;
import tdelivery.mr_irmag.order_service.domain.dto.calculationDelivery.CalculationDeliveryResponse;
import tdelivery.mr_irmag.order_service.domain.dto.calculationDelivery.RouteServiceResponse;
import tdelivery.mr_irmag.order_service.domain.dto.courierServiceDTO.NearestOrderResponseDto;
import tdelivery.mr_irmag.order_service.domain.dto.messageServiceDTO.MessageRequestDTO;
import tdelivery.mr_irmag.order_service.domain.dto.userServiceDTO.UserInfoResponseDTO;
import tdelivery.mr_irmag.order_service.domain.dto.userServiceDTO.UserOrderRequestDTO;
import tdelivery.mr_irmag.order_service.domain.entity.Order;
import tdelivery.mr_irmag.order_service.domain.entity.OrderItem;
import tdelivery.mr_irmag.order_service.domain.entity.OrderStatus;
import tdelivery.mr_irmag.order_service.exception.OrderNotFoundException;
import tdelivery.mr_irmag.order_service.exception.OrderProcessingException;
import tdelivery.mr_irmag.order_service.exception.UserServiceCommunicationException;
import tdelivery.mr_irmag.order_service.repository.OrderRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Log4j2
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderCacheService orderCacheService;
    private final UserServiceClient userServiceClient;
    private final MessageServiceClient messageServiceClient;
    private final RouteServiceClient routeServiceClient;
    private final KafkaProducerService kafkaProducerService;
    private final WebSocketDeliveryStatusService webSocketDeliveryStatusService;
    private final Gson gson;

    public CalculationDeliveryResponse calculateOrder(CalculateOrderRequest calculateOrderRequest) {
        RouteServiceResponse routeServiceResponse = routeServiceClient.calculateDelivery(calculateOrderRequest);
        CalculationDeliveryResponse response = CalculationDeliveryResponse.toCalculationDeliveryResponse(routeServiceResponse);

        log.info(response.toString());

        double productPrice = calculateOrderRequest.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        response.setProductPrice(productPrice);

        response.setTotalPrice(response.getDeliveryPrice() + productPrice);

        orderCacheService.cacheTotalAmount(calculateOrderRequest.getAddress(), response.getTotalPrice());
        orderCacheService.cacheRestaurantCoordinates(calculateOrderRequest.getAddress(), response.getRestaurantCoordinates());
        orderCacheService.cacheRestaurantAddress(calculateOrderRequest.getAddress(), response.getRestaurantAddress());
        orderCacheService.cacheDeliveryTime(calculateOrderRequest.getAddress(), response.getDeliveryDuration());
        orderCacheService.cacheUserCoordinates(calculateOrderRequest.getAddress(), response.getUserPoint());
        return response;
    }

    public List<UserOrderRequestDTO> getOrdersOfUser(UUID userId, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        Pageable pageable = PageRequest.of(page, size).withSort(sort);

        Page<Order> orderPage = orderRepository.findAllOrderByUserId(userId, pageable);

        if (orderPage.isEmpty()) {
            throw new OrderNotFoundException("No orders found for user with ID: " + userId);
        }

        List<UserOrderRequestDTO> orderDTOs = orderPage.getContent().stream()
                .map(Order::toUserOrderRequestDTO)
                .toList();

        log.info(orderDTOs.toString());
        return orderDTOs;
    }

    public UserInfoResponseDTO getUsernameAndEmailOfUserById(UUID userId) {
        try {
            String result = userServiceClient.getUserByID(userId);
            return gson.fromJson(result, UserInfoResponseDTO.class);
        } catch (Exception e) {
            throw new UserServiceCommunicationException("Failed to communicate with UserService" + e.getLocalizedMessage());
        }
    }

    public void processOrder(UUID id, CalculateOrderRequest calculateOrderRequest) {
        try {
            UserInfoResponseDTO userDTO = getUsernameAndEmailOfUserById(id);
            Double totalAmount = orderCacheService.getTotalAmount(calculateOrderRequest.getAddress());
            Point restaurantCoordinates = orderCacheService.getRestaurantCoordinates(calculateOrderRequest.getAddress());
            Point userCoordinates = orderCacheService.getUserCoordinates(calculateOrderRequest.getAddress());
            String restaurantAddress = orderCacheService.getRestaurantAddress(calculateOrderRequest.getAddress());
            Integer timeOfDelivery = orderCacheService.getDeliveryTime(calculateOrderRequest.getAddress());


            Order newOrder = Order.from(id, calculateOrderRequest, userDTO, totalAmount,
                    restaurantCoordinates, userCoordinates, restaurantAddress, timeOfDelivery);
            calculateOrderRequest.getItems().forEach(item -> log.info(item.toString()));
            List<OrderItem> orderItems = OrderItem.from(calculateOrderRequest.getItems(), newOrder);

            newOrder.setOrderItems(orderItems);
            log.info("Info: {}", newOrder.toString());

            var result = orderRepository.save(newOrder);

            log.info("Saved Order: {}", result.toString());

            int timeOfCooking = ChiefServiceClient.getTimeOfCooking();
            sendOrderEmail(userDTO.getEmail(), result, timeOfCooking);

            scheduleKafkaMessage(result, result.getEmail(), timeOfCooking);

            webSocketDeliveryStatusService.sendOrderStatusUpdate(newOrder.getStatus(), newOrder);

            orderCacheService.removeRestaurantCoordinates(calculateOrderRequest.getAddress());
            orderCacheService.removeTotalAmount(calculateOrderRequest.getAddress());
            orderCacheService.removeRestaurantAddress(calculateOrderRequest.getAddress());
            orderCacheService.removeDeliveryTime(calculateOrderRequest.getAddress());
        } catch (Exception e) {
            log.error("{} {} {}", e.getLocalizedMessage(), e.getClass().getName(), e.getMessage());
            throw new OrderProcessingException("Error processing order for user ID: " + id + e.getLocalizedMessage() + " " + e.getCause());
        }
    }

    public void sendEmail(UUID orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("No order found for id: " + orderId));
        UserInfoResponseDTO userDTO = getUsernameAndEmailOfUserById(order.getUserId());

        sendOrderEmail(userDTO.getEmail(), order, 1);
    }

    private void sendOrderEmail(String email, Order order, int timeOfCooking) {
        messageServiceClient.sendEmail(MessageRequestDTO.builder()
                .statusOfOrder(OrderStatus.PAID.toString())
                .email(email)
                .order(order.toMessageOrderDTO())
                .timeOfCooking(timeOfCooking)
                .build());
    }

    private void scheduleKafkaMessage(Order order, String email, int timeOfCooking) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            var result = KafkaDelayedMessageDTO.builder()
                    .orderId(order.getId())
                    .email(email)
                    .order(order.toMessageOrderDTO())
                    .build();
            kafkaProducerService.sendMessage("sheduled-message-order", gson.toJson(result));
        }, timeOfCooking, TimeUnit.SECONDS);
    }

    @Transactional
    public Order changeStatusOfOrder(UUID orderId, String stringStatus) {
        OrderStatus orderStatus = null;
        try {
            orderStatus = OrderStatus.valueOf(stringStatus);
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderStatusException("Invalid status: " + stringStatus);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("No orders found for user with ID: " + orderId));
        if(order.getStatus().equals(OrderStatus.CANCELED)){
            throw new OrderProcessingException("Order already cancelled");
        }
        order.setStatus(orderStatus);

        Order updatedOrder = orderRepository.save(order);
        webSocketDeliveryStatusService.sendOrderStatusUpdate(updatedOrder.getStatus(), updatedOrder);
        return updatedOrder;
    }

    public void updateSocket(ProcessCourierOrderRequest request){
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("No orders found for user with ID: " + request.getOrderId()));
        if(order.getStatus().equals(OrderStatus.CANCELED)){
            throw new OrderProcessingException("Order already cancelled");
        }
        order.setStatus(request.getOrderStatus());

        Order updatedOrder = orderRepository.save(order);

        webSocketDeliveryStatusService.sendCourierStatusUpdate(updatedOrder.getStatus(), updatedOrder, request);
    }

    public List<NearestOrderResponseDto> getNearestOrders(int radius, Point point) {
        var list = orderRepository.findNearestPreparedOrders(radius * 1000, point.getX(), point.getY(), 10);
        return NearestOrderResponseDto.from(list);
    }

    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("No order found with ID: " + orderId));
    }

}
