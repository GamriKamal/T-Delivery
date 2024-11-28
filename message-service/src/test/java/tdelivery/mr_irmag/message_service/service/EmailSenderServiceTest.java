package tdelivery.mr_irmag.message_service.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.MessagingException;
import tdelivery.mr_irmag.message_service.domain.dto.CourierMessageDto;
import tdelivery.mr_irmag.message_service.domain.dto.OrderDTO;
import tdelivery.mr_irmag.message_service.domain.dto.OrderItemDTO;
import tdelivery.mr_irmag.message_service.domain.dto.UserMessageRequestDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailSenderServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailSenderService emailSenderService;

    @Captor
    private ArgumentCaptor<MimeMessage> mimeMessageCaptor;

    @Test
    void sendPaidStatusMessage_ValidRequest_ShouldSendEmail() throws Exception {
        // Arrange
        OrderDTO order = OrderDTO.builder()
                .name("Test Order")
                .deliveryAddress("Test Address")
                .totalAmount(100.0)
                .orderItems(List.of(new OrderItemDTO("Item1", 2, 50.0, "desc")))
                .build();
        UserMessageRequestDTO request = UserMessageRequestDTO.builder()
                .statusOfOrder("PAID")
                .email("user@example.com")
                .order(order)
                .timeOfCooking(30)
                .build();
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        boolean result = emailSenderService.sendPaidStatusMessage(request);

        // Assert
        assertTrue(result);
        verify(mailSender).send(mimeMessageCaptor.capture());
        MimeMessage capturedMessage = mimeMessageCaptor.getValue();
        assertNotNull(capturedMessage);
    }

    @Test
    void sendPaidStatusMessage_MessagingException_ShouldThrowRuntimeException() throws Exception {
        // Arrange
        OrderDTO order = OrderDTO.builder()
                .name("Test Order")
                .deliveryAddress("Test Address")
                .totalAmount(100.0)
                .orderItems(List.of(new OrderItemDTO("Item1", 2, 50.0, "desc")))
                .build();
        UserMessageRequestDTO request = UserMessageRequestDTO.builder()
                .statusOfOrder("PAID")
                .email("user@example.com")
                .order(order)
                .timeOfCooking(30)
                .build();
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MessagingException("Test exception")).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> emailSenderService.sendPaidStatusMessage(request));
        assertNotNull(exception.getMessage());
    }

    @Test
    void sendShippedStatusMessage_ValidRequest_ShouldSendEmail() throws Exception {
        // Arrange
        OrderDTO order = OrderDTO.builder()
                .name("Test Order")
                .deliveryAddress("Test Address")
                .totalAmount(100.0)
                .orderItems(List.of(new OrderItemDTO("Item1", 2, 50.0, "desc")))
                .build();
        UserMessageRequestDTO request = UserMessageRequestDTO.builder()
                .statusOfOrder("PAID")
                .email("user@example.com")
                .order(order)
                .timeOfCooking(30)
                .build();
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailSenderService.sendShippedStatusMessage(request);

        // Assert
        verify(mailSender).send(mimeMessageCaptor.capture());
        MimeMessage capturedMessage = mimeMessageCaptor.getValue();
        assertNotNull(capturedMessage);
    }

    @Test
    void sendCourierPickupMessage_ValidRequest_ShouldSendEmail() throws Exception {
        // Arrange
        CourierMessageDto request = CourierMessageDto.builder()
                .email("user@example.com")
                .timeOfDelivery("15:00")
                .restaurantAddress("Main Street, 123")
                .orderStatus("ON_THE_WAY")
                .build();
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailSenderService.sendCourierPickupMessage(request);

        // Assert
        verify(mailSender).send(mimeMessageCaptor.capture());
        MimeMessage capturedMessage = mimeMessageCaptor.getValue();
        assertNotNull(capturedMessage);
    }

    @Test
    void sendOrderDeliveredMessage_ValidRequest_ShouldReturnTrue() throws Exception {
        // Arrange
        CourierMessageDto request = CourierMessageDto.builder()
                .email("user@example.com")
                .timeOfDelivery("15:00")
                .restaurantAddress("Main Street, 123")
                .orderStatus("ON_THE_WAY")
                .build();
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        boolean result = emailSenderService.sendOrderDeliveredMessage(request);

        // Assert
        assertTrue(result);
        verify(mailSender).send(mimeMessageCaptor.capture());
        MimeMessage capturedMessage = mimeMessageCaptor.getValue();
        assertNotNull(capturedMessage);
    }

}
