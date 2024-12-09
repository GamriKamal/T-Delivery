package tdelivery.mr_irmag.courier_service.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;


    @Test
    void sendMessage_ValidMessage_MessageSent() {
        // Arrange
        String topic = "test-topic";
        String message = "test-message";

        when(kafkaTemplate.send(topic, message)).thenReturn(mock(CompletableFuture.class));

        // Act
        kafkaProducerService.sendMessage(topic, message);

        // Assert
        verify(kafkaTemplate, times(1)).send(topic, message);
    }

    @Test
    void sendMessage_KafkaTemplateThrowsException_ThrowsKafkaException() {
        // Arrange
        String topic = "test-topic";
        String message = "test-message";
        when(kafkaTemplate.send(topic, message)).thenThrow(new KafkaException("Kafka error"));

        // Act & Assert
        KafkaException exception = assertThrows(KafkaException.class,
                () -> kafkaProducerService.sendMessage(topic, message));

        assertEquals("Kafka error", exception.getMessage());
        verify(kafkaTemplate, times(1)).send(topic, message);
    }
}