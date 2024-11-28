package tdelivery.mr_irmag.order_service.kafka;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tdelivery.mr_irmag.order_service.domain.dto.KafkaDelayedMessageDTO;
import tdelivery.mr_irmag.order_service.domain.dto.messageServiceDTO.MessageRequestDTO;
import tdelivery.mr_irmag.order_service.domain.entity.OrderStatus;
import tdelivery.mr_irmag.order_service.service.MessageServiceClient;
import tdelivery.mr_irmag.order_service.service.OrderService;


@Service
@Log4j2
public class KafkaConsumerService {
    private final Gson gson;
    private final MessageServiceClient messageServiceClient;
    private final OrderService orderService;

    @Autowired
    public KafkaConsumerService(Gson gson, MessageServiceClient messageServiceClient,
                                OrderService orderService) {
        this.gson = gson;
        this.messageServiceClient = messageServiceClient;
        this.orderService = orderService;
    }

    @KafkaListener(topics = "sheduled-message-order", groupId = "order-group")
    public void listen(String message) {
        log.info("Received message: " + message);

        KafkaDelayedMessageDTO messageDTO = gson.fromJson(message, KafkaDelayedMessageDTO.class);

        if(orderService.getOrderById(messageDTO.getOrderId()).getStatus() != OrderStatus.CANCELED) {
            var result = orderService.changeStatusOfOrder(messageDTO.getOrderId(), "PREPARED");

            messageServiceClient.sendEmail(MessageRequestDTO.builder()
                    .statusOfOrder(result.getStatus().toString())
                    .email(messageDTO.getEmail())
                    .order(messageDTO.getOrder())
                    .build());
        }
    }
}

