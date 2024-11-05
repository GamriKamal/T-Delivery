package tdelivery.mr_irmag.message_service.Kafka;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tdelivery.mr_irmag.message_service.domain.dto.CourierMessageDto;
import tdelivery.mr_irmag.message_service.domain.dto.UserMessageRequestDTO;
import tdelivery.mr_irmag.message_service.service.EmailSenderService;

@Service
@Log4j2
public class KafkaConsumerService {
    private final Gson gson;
    private final EmailSenderService emailSenderService;

    @Autowired
    public KafkaConsumerService(Gson gson, EmailSenderService emailSenderService) {
        this.gson = gson;
        this.emailSenderService = emailSenderService;
    }

    @KafkaListener(topics = "order-message", groupId = "message-group")
    public void listen(String message) {
        log.info("Received message: " + message);
        var result = gson.fromJson(message, UserMessageRequestDTO.class);
        if(result.getStatusOfOrder().equals("PAID")) {
            emailSenderService.sendPaidStatusMessage(result);
        } else if(result.getStatusOfOrder().equals("PREPARED")) {
            emailSenderService.sendShippedStatusMessage(result);
        }
    }

    @KafkaListener(topics = "courier-topic", groupId = "message-group")
    public void listenCourier(String message) {
        log.info("Received message: " + message);
        var result = gson.fromJson(message, CourierMessageDto.class);
        if(result.getOrderStatus().equals("SHIPPED")){
            emailSenderService.sendCourierPickupMessage(result);
        } else if(result.getOrderStatus().equals("DELIVERED")){
            emailSenderService.sendOrderDeliveredMessage(result);
        }
    }
}

