package tdelivery.mr_irmag.order_service.service;

import lombok.extern.log4j.Log4j2;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import tdelivery.mr_irmag.order_service.domain.dto.DeliveryStatus;
import tdelivery.mr_irmag.order_service.domain.dto.ProcessCourierOrderRequest;
import tdelivery.mr_irmag.order_service.domain.entity.Order;
import tdelivery.mr_irmag.order_service.domain.entity.OrderStatus;


import java.util.UUID;

@Service
@Log4j2
public class WebSocketDeliveryStatusService {
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public DeliveryStatus sendOrderStatusUpdate(OrderStatus orderStatus, Order order) {
        boolean showMap = orderStatus.equals(OrderStatus.SHIPPED);
        DeliveryStatus message = getDeliveryStatusMessage(orderStatus, order.getDeliveryAddress(), showMap);
        messagingTemplate.convertAndSend("/topic/deliveryStatus", message);
        return message;
    }

    public DeliveryStatus sendCourierStatusUpdate(OrderStatus orderStatus, Order order, ProcessCourierOrderRequest request) {
        boolean showMap = orderStatus.equals(OrderStatus.SHIPPED);
        DeliveryStatus message = getDeliveryStatusMessage(orderStatus, order.getDeliveryAddress(), order.getRestaurantCoordinates(),
                order.getUserCoordinates(), request.getCourierPoint(), Long.valueOf(order.getTimeOfDelivery() / 60),
                Long.valueOf(request.getTimeDelivery()), showMap, order.getId());
        messagingTemplate.convertAndSend("/topic/deliveryStatus", message);
        return message;
    }


    private DeliveryStatus getDeliveryStatusMessage(OrderStatus status, String address, boolean showMap) {
        String message;
        String imageUrl;

        switch (status) {
            case PAID:
                message = "Ваш заказ успешно оплачен и в данный момент готовится к отправке.";
                imageUrl = "/images/paid.gif";
                break;
            case PREPARED:
                message = "Отличные новости! Ваш заказ готов, и мы ищем курьера для его доставки.";
                imageUrl = "/images/prepared.gif";
                break;
            case SHIPPED:
                message = "Курьер уже забрал ваш заказ и направляется к вам. Ожидайте!";
                imageUrl = "/images/shipped.gif";
                break;
            case DELIVERED:
                message = "Ваш заказ был успешно доставлен! Надеемся, вам все понравится.";
                imageUrl = "/images/delivered.gif";
                break;
            case CANCELED:
                message = "К сожалению, ваш заказ был отменен. Если у вас есть вопросы, пожалуйста, свяжитесь с нами.";
                imageUrl = "/images/canceled.gif";
                break;
            default:
                throw new IllegalStateException("Неизвестный статус: " + status);
        }

        return DeliveryStatus.builder()
                .message(message + " Адрес доставки: " + address)
                .imageUrl(imageUrl)
                .showMap(showMap)
                .orderStatus(status)
                .build();
    }

    private DeliveryStatus getDeliveryStatusMessage(OrderStatus status, String address, Point restaurantCoordinates,
                                                    Point userCoordinates, org.springframework.data.geo.Point courierCoordinates,
                                                    Long timeToUser, Long timeToRestaurant, boolean showMap, UUID orderId) {
        String message;
        String imageUrl;

        double courierLat = courierCoordinates.getX();
        double courierLng = courierCoordinates.getY();
        double restaurantLat = restaurantCoordinates.getX();
        double restaurantLng = restaurantCoordinates.getY();
        double userLat = userCoordinates.getX();
        double userLng = userCoordinates.getY();

        switch (status) {
            case PAID:
                message = "Ваш заказ успешно оплачен и в данный момент готовится к отправке.";
                imageUrl = "/images/paid.gif";
                break;
            case PREPARED:
                message = "Отличные новости! Ваш заказ готов, и мы ищем курьера для его доставки.";
                imageUrl = "/images/prepared.gif";
                break;
            case SHIPPED:
                message = "Курьер уже забрал ваш заказ и направляется к вам. Ожидайте!";
                imageUrl = "/images/shipped.gif";
                break;
            case DELIVERED:
                message = "Ваш заказ был успешно доставлен! Надеемся, вам все понравится.";
                imageUrl = "/images/delivered.gif";
                break;
            case CANCELED:
                message = "К сожалению, ваш заказ был отменен. Если у вас есть вопросы, пожалуйста, свяжитесь с нами.";
                imageUrl = "/images/canceled.gif";
                break;
            default:
                throw new IllegalStateException("Неизвестный статус: " + status);
        }

        var example = DeliveryStatus.builder()
                .orderId(orderId)
                .message(message + " Адрес доставки: " + address)
                .imageUrl(imageUrl)
                .showMap(showMap)
                .orderStatus(status)
                .courierLat(courierLat)
                .courierLng(courierLng)
                .restaurantLat(restaurantLat)
                .restaurantLng(restaurantLng)
                .userLat(userLat)
                .userLng(userLng)
                .timeToRestaurant(timeToRestaurant * 6)
                .timeToUser(timeToUser * 6)
                .build();

        log.info(example.toString());
        return example;
    }

}

