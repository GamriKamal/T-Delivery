package tdelivery.mr_irmag.order_service.Kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String message) {
        try {
            kafkaTemplate.send(topic, message);
        } catch (Exception e) {
            log.error("Error while sending message {}", e.getLocalizedMessage());
            throw new KafkaException(e.getLocalizedMessage());
        }
    }

}
