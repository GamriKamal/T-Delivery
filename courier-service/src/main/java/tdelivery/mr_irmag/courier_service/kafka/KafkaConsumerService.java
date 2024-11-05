package tdelivery.mr_irmag.courier_service.kafka;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tdelivery.mr_irmag.courier_service.domain.dto.KafkaDelayedMessageDTO;
import tdelivery.mr_irmag.courier_service.domain.dto.MessageRequestDto;
import tdelivery.mr_irmag.courier_service.domain.entity.OrderStatus;
import tdelivery.mr_irmag.courier_service.service.MessageServiceClient;
import tdelivery.mr_irmag.courier_service.service.OrderServiceClient;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KafkaConsumerService {
    private final Gson gson;
    private final MessageServiceClient messageServiceClient;
    private final OrderServiceClient orderServiceClient;

    @KafkaListener(topics = "sheduled-courier-message-order", groupId = "order-group")
    public void listen(String message) {
        log.info("Received message: " + message);

        KafkaDelayedMessageDTO messageDTO = gson.fromJson(message, KafkaDelayedMessageDTO.class);

        HttpStatusCode code = orderServiceClient.changeStatusOfrOrder(messageDTO.getOrderId(), OrderStatus.DELIVERED);

        if(code.is2xxSuccessful()){
            messageServiceClient.sendEmail(MessageRequestDto.builder()
                    .orderStatus(messageDTO.getOrderStatus())
                    .email(messageDTO.getEmail())
                    .timeOfDelivery(String.valueOf(messageDTO.getTime()))
                    .build());
        }
    }
}

