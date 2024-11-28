package tdelivery.mr_irmag.courier_service.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tdelivery.mr_irmag.courier_service.domain.dto.KafkaDelayedMessageDTO;
import tdelivery.mr_irmag.courier_service.domain.dto.MessageRequestDto;
import tdelivery.mr_irmag.courier_service.exception.MessageEmptyException;
import tdelivery.mr_irmag.courier_service.kafka.KafkaProducerService;

@Service
@RequiredArgsConstructor
@Log4j2
public class MessageServiceClient {
    private final KafkaProducerService kafkaProducerService;
    private final Gson gson;
    @Value("${tdelivery.message-topic}")
    private String messageServiceTopic;

    public void sendEmail(MessageRequestDto message) {
        if (message == null) {
            log.error("Provided order for sending message is null!");
            throw new MessageEmptyException("Provided message for sending message is null!");
        }

        log.info("Sending message to {}", messageServiceTopic);
        message.changeTime();
        kafkaProducerService.sendMessage(messageServiceTopic, gson.toJson(message));
        log.info("Message sent successfully! {}", message);
    }


    public void sendKafkaMessage(String topic, KafkaDelayedMessageDTO message) {
        kafkaProducerService.sendMessage(topic, gson.toJson(message));
    }

}
