package tdelivery.mr_irmag.order_service.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import tdelivery.mr_irmag.order_service.domain.dto.*;
import tdelivery.mr_irmag.order_service.domain.entity.Order;
import tdelivery.mr_irmag.order_service.domain.entity.OrderItem;
import tdelivery.mr_irmag.order_service.domain.entity.OrderStatus;
import tdelivery.mr_irmag.order_service.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Log4j2
public class OrderService {
    private final DistanceService distanceService;
    private final OrderRepository orderRepository;
    private final OrderCacheService orderCacheService;
    private final UserServiceClient userServiceClient;
    private final MessageServiceClient messageServiceClient;
    private final Gson gson;

    public DeliveryDTO calculateOrder(UUID userId, OrderRequest orderRequest) {
//        DeliveryDTO response = distanceService.calculateDelivery(orderRequest.getAddress());
//
//        double productPrice = orderRequest.getItems().stream()
//                .mapToDouble(item -> item.getPrice() * item.getQuantity())
//                .sum();
//        response.setProductPrice(productPrice);
//
//        response.setTotalPrice(response.getDeliveryPrice() + productPrice);
//
//        orderCacheService.cacheTotalAmount(userId, response.getTotalPrice());
//        return response;

        orderCacheService.cacheTotalAmount(userId, 100.0);
        return DeliveryDTO.builder().deliveryDuration("privet").build();
    }

    public List<UserOrderDTO> getOrdersOfUser(UUID userId, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        Pageable pageable = PageRequest.of(page, size).withSort(sort);

        Page<Order> orderPage = orderRepository.findAllOrderByUserId(userId, pageable);

        List<UserOrderDTO> orderDTOs = orderPage.getContent().stream().map(order -> UserOrderDTO.builder()
                .name(order.getName())
                .createdDate(order.getCreatedDate())
                .comment(order.getComment())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .deliveryAddress(order.getDeliveryAddress())
                .build()
        ).toList();

        log.info(orderDTOs.toString());

        return orderDTOs;
    }

    public UserDTO getUsernameAndEmailOfUserById(UUID userId) {
        String result = userServiceClient.getUserByID(userId);
        return gson.fromJson(result, UserDTO.class);
    }

    public void processOrder(UUID id, @RequestBody OrderRequest orderRequest) {
        UserDTO userDTO = getUsernameAndEmailOfUserById(id);
        Order newOrder = Order.builder()
                .name("Order of " + userDTO.getUsername() + ": " +
                        orderRequest.getItems().stream()
                                .map(OrderItemRequest::getName)
                                .collect(Collectors.joining(", ")))
                .createdDate(LocalDateTime.now())
                .deliveryAddress(orderRequest.getAddress())
                .comment(orderRequest.getComment())
                .totalAmount(orderCacheService.getTotalAmount(id))
                .status(OrderStatus.PAID)
                .userId(id)
                .email(userDTO.getEmail())
                .build();

        List<OrderItem> orderItems = orderRequest.getItems().stream()
                .map(item -> OrderItem.builder()
                        .name(item.getName())
                        .price(item.getPrice())
                        .description(item.getDescription())
                        .order(newOrder)
                        .build())
                .collect(Collectors.toList());

        newOrder.setOrderItems(orderItems);

        orderRepository.save(newOrder);

        messageServiceClient.sendEmail("startOfOrder", userDTO.getEmail(), newOrder, 20);
    }

}
