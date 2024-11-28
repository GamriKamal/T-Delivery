package tdelivery.mr_irmag.courier_service.advice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonParseException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.KafkaException;
import tdelivery.mr_irmag.courier_service.domain.dto.ErrorResponse;
import tdelivery.mr_irmag.courier_service.exception.RouteServiceException;
import tdelivery.mr_irmag.courier_service.exception.ExternalServiceException;
import tdelivery.mr_irmag.courier_service.exception.MessageEmptyException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.mockito.Mockito;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleRouteServiceException_WhenRouteServiceError_ShouldReturnInternalServerError() {
        // Arrange
        RouteServiceException ex = new RouteServiceException("Route service error");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleRouteServiceException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Route service error", response.getBody().getMessage());
    }

    @Test
    void handleExternalServiceException_WhenExternalServiceError_ShouldReturnBadGateway() {
        // Arrange
        ExternalServiceException ex = new ExternalServiceException("External service error");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleExternalServiceException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertEquals("External service error", response.getBody().getMessage());
    }

    @Test
    void handleMessageEmptyException_WhenMessageIsEmpty_ShouldReturnBadRequest() {
        // Arrange
        MessageEmptyException ex = new MessageEmptyException("Message is empty");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMessageEmptyException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Message is empty", response.getBody().getMessage());
    }

    @Test
    void handleKafkaException_WhenKafkaErrorOccurs_ShouldReturnInternalServerError() {
        // Arrange
        KafkaException ex = new KafkaException("Kafka error");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleKafkaException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Kafka error", response.getBody().getMessage());
    }

    @Test
    void handleJsonParseException_WhenJsonParseErrorOccurs_ShouldReturnBadRequest() {
        // Arrange
        JsonParseException ex = new JsonParseException("JSON parse error");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleJsonParseException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("JSON parse error", response.getBody().getMessage());
    }

    @Test
    void handleHttpClientErrorException_WhenHttpClientErrorOccurs_ShouldReturnInternalServerError() {
        // Arrange
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "HTTP client error",
                null, null, null
        );

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleHttpClientErrorException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("500 HTTP client error", response.getBody().getMessage());
    }

    @Test
    void handleNullPointerException_WhenNullPointerOccurs_ShouldReturnInternalServerError() {
        // Arrange
        NullPointerException ex = new NullPointerException("Null pointer exception");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNullPointerException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Null pointer exception", response.getBody().getMessage());
    }

    @Test
    void handleGeneralException_WhenUnexpectedErrorOccurs_ShouldReturnInternalServerError() {
        // Arrange
        Exception ex = new Exception("Unexpected error occurred");
        WebRequest request = mock(WebRequest.class);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGeneralException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred: Unexpected error occurred", response.getBody().getMessage());
    }
}
