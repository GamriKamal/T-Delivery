package tdelivery.mr_irmag.order_service.service;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tdelivery.mr_irmag.order_service.TestContainerBase;
import tdelivery.mr_irmag.order_service.kafka.KafkaProducerService;
import tdelivery.mr_irmag.order_service.domain.dto.messageServiceDTO.MessageOrderDTO;
import tdelivery.mr_irmag.order_service.domain.dto.messageServiceDTO.MessageRequestDTO;
import tdelivery.mr_irmag.order_service.exception.OrderEmptyException;
import org.junit.jupiter.api.BeforeEach;
import com.google.gson.Gson;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@SpringBootTest
@Testcontainers
@TestPropertySource(properties = "eureka.client.enabled=false")
class MessageServiceClientTest extends TestContainerBase {
    private static final Logger log = LoggerFactory.getLogger(MessageServiceClientTest.class);

    @MockBean
    private KafkaProducerService kafkaProducerService;

    @MockBean
    private Gson gson;

    @Autowired
    private MessageServiceClient messageServiceClient;

    private MessageRequestDTO testMessageRequestDTO;

    @BeforeEach
    public void setup() {
        testMessageRequestDTO = MessageRequestDTO.builder()
                .statusOfOrder("PAID")
                .email("test@example.com")
                .order(new MessageOrderDTO())
                .timeOfCooking(5)
                .build();

        when(gson.toJson(any(MessageRequestDTO.class)))
                .thenReturn("{\"email\":\"test@example.com\",\"message\":\"Test message\"}");
    }

    @Test
    public void sendEmail_NullMessageRequestDTO_ShouldThrowOrderEmptyException() {
        // Act & Assert
        assertThrows(OrderEmptyException.class, () -> messageServiceClient.sendEmail(null));
    }

    @Test
    public void sendEmail_ValidMessageRequestDTO_ShouldLogInfoMessage() {
        // Arrange:
        testMessageRequestDTO = new MessageRequestDTO("PAID", "test@example.com", new MessageOrderDTO(), 5);

        // Act
        messageServiceClient.sendEmail(testMessageRequestDTO);

        // Assert
        log.info("Message sent successfully! {}", testMessageRequestDTO.toString());
    }

    @Test
    public void sendEmail_NullMessageRequestDTO_ShouldLogErrorMessage() {
        // Act
        try {
            messageServiceClient.sendEmail(null);
        } catch (OrderEmptyException e) {
            // Assert
            log.error("Provided order for sending message is null!");
        }
    }
}
