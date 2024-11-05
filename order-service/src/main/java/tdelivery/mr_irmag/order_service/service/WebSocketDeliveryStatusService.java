package tdelivery.mr_irmag.order_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tdelivery.mr_irmag.order_service.domain.dto.DeliveryStatus;
import tdelivery.mr_irmag.order_service.domain.entity.Order;
import tdelivery.mr_irmag.order_service.domain.entity.OrderStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tdelivery.mr_irmag.order_service.domain.dto.DeliveryStatus;
import tdelivery.mr_irmag.order_service.domain.entity.OrderStatus;

@Service
public class WebSocketDeliveryStatusService {
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public DeliveryStatus sendOrderStatusUpdate(OrderStatus orderStatus, Order order) {
        DeliveryStatus message = getDeliveryStatusMessage(orderStatus, order.getDeliveryAddress());
        messagingTemplate.convertAndSend("/topic/deliveryStatus", message);
        return message;
    }

    private DeliveryStatus getDeliveryStatusMessage(OrderStatus status, String address) {
        String message;
        String imageUrl;
        int progress;

        switch (status) {
            case PAID:
                message = String.format("Ваш заказ успешно оплачен и в данный момент готовится к отправке.");
                imageUrl = "/images/paid.gif";
                progress = 25;
                break;
            case PREPARED:
                message = String.format("Отличные новости! Ваш заказ готов, и мы ищем курьера для его доставки.");
                imageUrl = "/images/prepared.gif";
                progress = 50;
                break;
            case SHIPPED:
                message = String.format("Курьер уже забрал ваш заказ и направляется к вам. Ожидайте!");
                imageUrl = "/images/shipped.gif";
                progress = 75;
                break;
            case DELIVERED:
                message = String.format("Ваш заказ был успешно доставлен! Надеемся, вам все понравится.");
                imageUrl = "/images/delivered.gif";
                progress = 100;
                break;
            case CANCELED:
                message = String.format("К сожалению, ваш заказ был отменен. Если у вас есть вопросы, пожалуйста, свяжитесь с нами.");
                imageUrl = "/images/canceled.gif";
                progress = 0;
                break;
            default:
                throw new IllegalStateException("Неизвестный статус: " + status);
        }

        return new DeliveryStatus(message + " Адрес доставки: " + address, imageUrl, progress);
    }

}

