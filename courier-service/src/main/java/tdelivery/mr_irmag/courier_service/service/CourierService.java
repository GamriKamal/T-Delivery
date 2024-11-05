package tdelivery.mr_irmag.courier_service.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import tdelivery.mr_irmag.courier_service.domain.dto.*;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.GetNearestOrderResponse;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.NearestOrderRequestDto;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.OrderForRouteDto;
import tdelivery.mr_irmag.courier_service.domain.entity.Order;
import tdelivery.mr_irmag.courier_service.domain.entity.OrderStatus;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class CourierService {
    private final OrderServiceClient orderServiceClient;
    private final RouteServiceClient routeServiceClient;
    private final CourierCacheService courierCacheService;
    private final MessageServiceClient messageServiceClient;

    public GetNearestOrderResponse getNearestOrders(NearestOrderRequestDto request){
        List<Order> orders = orderServiceClient.getNearestOrders(request);
        if(orders.isEmpty()) return null;

        RouteServiceResponse optimalOrder = routeServiceClient.findNearestOrder(RouteServiceRequest.builder()
                .ordersForRoute(OrderForRouteDto.from(orders))
                .courierCoordinates(request.getPoint())
                .build());

        Order foundOrder = orders.stream()
                .filter(order -> order.getId().equals(optimalOrder.getId()))
                .findFirst()
                .orElse(null);
        
        if(foundOrder != null){
            courierCacheService.cacheOptimalOrder(request.getPoint(), foundOrder);
        }

        if(optimalOrder != null){
            courierCacheService.cacheTimeDelivery(request.getPoint(), optimalOrder);
        }
        
        return GetNearestOrderResponse.from(foundOrder, optimalOrder);
    }

    public void takeOrder(NearestOrderRequestDto request){
        Order order = courierCacheService.getOptimalOrder(request.getPoint());
        RouteServiceResponse timeDelivery = courierCacheService.getTimeDelivery(request.getPoint());

        HttpStatusCode statusCode = orderServiceClient.changeStatusOfrOrder(order.getId(), OrderStatus.SHIPPED);

        if(statusCode.is2xxSuccessful()){
            messageServiceClient.sendEmail(MessageRequestDto.builder()
                    .timeOfDelivery(String.valueOf(timeDelivery.getDuration().getValue() / 60) + " minutes")
                    .orderStatus("SHIPPED")
                    .email(order.getEmail())
                    .restaurantAddress(order.getRestaurantAddress())
                    .build());

            scheduleKafkaMessage(order.getId(), order.getEmail(), timeDelivery.getDuration().getValue() / 60);
        }

    }

    private void scheduleKafkaMessage(UUID orderId, String email, int timeOfDelivery) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            var result = KafkaDelayedMessageDTO.builder()
                    .orderId(orderId)
                    .orderStatus("DELIVERED")
                    .email(email)
                    .time(timeOfDelivery)
                    .build();
            messageServiceClient.sendKafkaMessage("sheduled-courier-message-order", result);
        }, timeOfDelivery, TimeUnit.SECONDS);
    }
}
