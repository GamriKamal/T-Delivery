package tdelivery.mr_irmag.courier_service.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.kafka.KafkaException;
import org.springframework.stereotype.Service;
import tdelivery.mr_irmag.courier_service.domain.dto.*;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.GetNearestOrderResponse;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.NearestOrderRequestDto;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.OrderForRouteDto;
import tdelivery.mr_irmag.courier_service.domain.entity.Order;
import tdelivery.mr_irmag.courier_service.domain.entity.OrderStatus;
import tdelivery.mr_irmag.courier_service.exception.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class CourierService {
    private final OrderServiceClient orderServiceClient;
    private final RouteServiceClient routeServiceClient;
    private final CourierCacheService courierCacheService;
    private final MessageServiceClient messageServiceClient;

    public List<GetNearestOrderResponse> getNearestOrders(NearestOrderRequestDto request) {
        log.info(request.toString());
        log.info(orderServiceClient.getNearestOrders(request) + " privet");
        List<Order> orders = orderServiceClient.getNearestOrders(request);
        if (orders.isEmpty()) {
            throw new OrderNotFoundException("No orders found near the requested location.");
        }

        RouteServiceResponse optimalOrder;
        try {
            optimalOrder = routeServiceClient.findNearestOrder(RouteServiceRequest.builder()
                    .ordersForRoute(OrderForRouteDto.from(orders))
                    .courierCoordinates(request.getPoint())
                    .build());
        } catch (RouteServiceException e) {
            throw new RouteServiceException("Error fetching nearest order from Route Service.");
        }

        List<Order> foundOrders = orders.stream()
                .filter(order -> order.getId().equals(optimalOrder.getId()))
                .limit(5)
                .collect(Collectors.toList());

        try {
            courierCacheService.cacheOptimalOrders(request.getPoint(), foundOrders);
            courierCacheService.cacheTimeDelivery(request.getPoint(), optimalOrder);
        } catch (Exception e) {
            throw new CourierCacheException("Error caching order details in Courier Cache.");
        }

        return GetNearestOrderResponse.from(foundOrders, optimalOrder);
    }


    public void takeOrder(GetOrderRequest request) {
        List<Order> order = courierCacheService.getOptimalOrder(request.getPoint());
        Order nearestOrder = order.stream()
                .filter(order1 -> order1.getId().equals(request.getOrderId()))
                .findFirst()
                .orElseThrow(() -> new OrderNotFoundException("No order found with id " + request.getOrderId()));

        RouteServiceResponse timeDelivery = courierCacheService.getTimeDelivery(request.getPoint());
        if (timeDelivery == null) {
            throw new CourierCacheException("No time delivery data found in cache.");
        }

        HttpStatusCode statusCode = orderServiceClient.changeStatusOfOrder(ProcessCourierOrderRequest.builder()
                        .orderId(nearestOrder.getId())
                        .courierPoint(request.getPoint())
                        .orderStatus(OrderStatus.SHIPPED)
                        .timeDelivery(timeDelivery.getDuration().getValue() / 60)
                .build());
        if (!statusCode.is2xxSuccessful()) {
            throw new ExternalServiceException("Failed to update order status to SHIPPED.");
        }

        courierCacheService.removeOptimalOrder(request.getPoint());
        courierCacheService.removeTimeDelivery(request.getPoint());

        try {
            messageServiceClient.sendEmail(MessageRequestDto.builder()
                    .timeOfDelivery(timeDelivery.getDuration().getValue() / 60 + " minutes")
                    .orderStatus("SHIPPED")
                    .email(nearestOrder.getEmail())
                    .restaurantAddress(nearestOrder.getRestaurantAddress())
                    .build());
        } catch (MessageEmptyException | KafkaException e) {
            throw new ExternalServiceException("Failed to send email notification.");
        }

    }

}
