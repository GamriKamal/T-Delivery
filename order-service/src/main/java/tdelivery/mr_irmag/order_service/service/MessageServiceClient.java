package tdelivery.mr_irmag.order_service.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tdelivery.mr_irmag.order_service.kafka.KafkaProducerService;
import tdelivery.mr_irmag.order_service.domain.dto.messageServiceDTO.MessageRequestDTO;
import tdelivery.mr_irmag.order_service.exception.OrderEmptyException;

@Service
@RequiredArgsConstructor
@Log4j2
public class MessageServiceClient {
    @Value("${tdelivery.messageService.topic}")
    private String messageServiceTopic;

    private final KafkaProducerService kafkaProducerService;
    private final Gson gson;

    public void sendEmail(MessageRequestDTO messageRequestDTO) {
        if(messageRequestDTO == null){
            log.error("Provided order for sending message is null!");
            throw new OrderEmptyException("Provided order for sending message is null!");
        }

        kafkaProducerService.sendMessage(messageServiceTopic, gson.toJson(messageRequestDTO));
        log.info("Message sent successfully! {}", messageRequestDTO.toString());
    }
}
