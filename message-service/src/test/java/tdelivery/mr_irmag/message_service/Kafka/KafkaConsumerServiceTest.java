package tdelivery.mr_irmag.message_service.Kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import tdelivery.mr_irmag.message_service.KafkaContainerTestBase;
import tdelivery.mr_irmag.message_service.domain.dto.CourierMessageDto;
import tdelivery.mr_irmag.message_service.domain.dto.UserMessageRequestDTO;
import tdelivery.mr_irmag.message_service.service.EmailSenderService;

import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.*;

@SpringBootTest
@Testcontainers
class KafkaConsumerServiceTest extends KafkaContainerTestBase {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    @MockBean
    private EmailSenderService emailSenderService;

    @Test
    void listen_MessageIsPaid_ShouldCallSendPaidStatusMessage() throws InterruptedException {
        // Arrange
        String topic = "order-message";
        String message = "{\"statusOfOrder\":\"PAID\"}";

        // Act
        kafkaTemplate.send(topic, message);
        Thread.sleep(2000);

        // Assert
        verify(emailSenderService, times(1)).sendPaidStatusMessage(any());
    }

    @Test
    void listen_MessageIsPrepared_ShouldCallSendShippedStatusMessage() throws InterruptedException {
        // Arrange
        String topic = "order-message";
        String message = "{\"statusOfOrder\":\"PREPARED\"}";

        // Act
        kafkaTemplate.send(topic, message);
        Thread.sleep(2000);

        // Assert
        verify(emailSenderService, times(1)).sendShippedStatusMessage(any(UserMessageRequestDTO.class));
        verify(emailSenderService, never()).sendPaidStatusMessage(any());
    }

    @Test
    void listenCourier_MessageIsShipped_ShouldCallSendCourierPickupMessage() throws InterruptedException {
        // Arrange
        String topic = "courier-topic";
        String message = "{\"orderStatus\":\"SHIPPED\"}";

        // Act
        kafkaTemplate.send(topic, message);
        Thread.sleep(2000);

        // Assert
        verify(emailSenderService, times(1)).sendCourierPickupMessage(any(CourierMessageDto.class));
        verify(emailSenderService, never()).sendOrderDeliveredMessage(any());
    }

    @Test
    void listenCourier_MessageIsDelivered_ShouldCallSendOrderDeliveredMessage() throws InterruptedException {
        // Arrange
        String topic = "courier-topic";
        String message = "{\"orderStatus\":\"DELIVERED\"}";

        // Act
        kafkaTemplate.send(topic, message);
        Thread.sleep(2000);

        // Assert
        verify(emailSenderService, times(1)).sendOrderDeliveredMessage(any(CourierMessageDto.class));
        verify(emailSenderService, never()).sendCourierPickupMessage(any());
    }

    @Test
    void listenCourier_MessageIsUnknown_ShouldNotCallAnyService() throws InterruptedException {
        // Arrange
        String topic = "courier-topic";
        String message = "{\"orderStatus\":\"SMTH\"}";

        // Act
        kafkaTemplate.send(topic, message);
        Thread.sleep(2000);

        // Assert
        verifyNoInteractions(emailSenderService);
    }
}

