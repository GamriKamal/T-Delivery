package tdelivery.mr_irmag.courier_service.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tdelivery.mr_irmag.courier_service.domain.dto.KafkaDelayedMessageDTO;
import tdelivery.mr_irmag.courier_service.domain.dto.MessageRequestDto;
import tdelivery.mr_irmag.courier_service.exception.MessageEmptyException;
import tdelivery.mr_irmag.courier_service.kafka.KafkaProducerService;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MessageServiceClientTest {

    private final String messageServiceTopic = "mock-topic";
    @Mock
    private KafkaProducerService kafkaProducerService;
    @Mock
    private Gson gson;
    @InjectMocks
    private MessageServiceClient messageServiceClient;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        Field field = MessageServiceClient.class.getDeclaredField("messageServiceTopic");
        field.setAccessible(true);
        field.set(messageServiceClient, messageServiceTopic);
    }

    @Test
    void sendEmail_ValidMessage_ShouldSendSuccessfully() {
        // Arrange
        MessageRequestDto mockMessage = MessageRequestDto.builder()
                .timeOfDelivery("5 minutes")
                .email("someEmail")
                .restaurantAddress("someRestaurantAddress")
                .orderStatus("PAID")
                .build();

        mockMessage.changeTime();
        String mockMessageJson = "{\"message\":\"test message\"}";

        when(gson.toJson(mockMessage)).thenReturn(mockMessageJson);

        // Act
        messageServiceClient.sendEmail(mockMessage);

        // Assert
        verify(gson, times(1)).toJson(mockMessage);
        verify(kafkaProducerService, times(1)).sendMessage(messageServiceTopic, mockMessageJson);
    }


    @Test
    void sendEmail_NullMessage_ShouldThrowMessageEmptyException() {
        // Arrange
        MessageRequestDto nullMessage = null;

        // Act & Assert
        Exception exception = assertThrows(MessageEmptyException.class, () -> {
            messageServiceClient.sendEmail(nullMessage);
        });

        assertEquals("Provided message for sending message is null!", exception.getMessage());
        verify(kafkaProducerService, never()).sendMessage(anyString(), anyString());
    }

    @Test
    void sendKafkaMessage_ValidTopicAndMessage_ShouldSendSuccessfully() {
        // Arrange
        String topic = "another-topic";
        KafkaDelayedMessageDTO mockKafkaMessage = new KafkaDelayedMessageDTO();
        String mockMessageJson = "{\"delayedMessage\":\"test delayed message\"}";

        when(gson.toJson(mockKafkaMessage)).thenReturn(mockMessageJson);

        // Act
        messageServiceClient.sendKafkaMessage(topic, mockKafkaMessage);

        // Assert
        verify(gson, times(1)).toJson(mockKafkaMessage);
        verify(kafkaProducerService, times(1)).sendMessage(topic, mockMessageJson);
    }
}