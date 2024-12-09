package tdelivery.mr_irmag.auth_service.advice;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import tdelivery.mr_irmag.auth_service.domain.dto.ErrorResponse;
import tdelivery.mr_irmag.auth_service.exceptions.InvalidRequestException;
import tdelivery.mr_irmag.auth_service.exceptions.ServerException;
import tdelivery.mr_irmag.auth_service.exceptions.ServiceUnavailableException;
import tdelivery.mr_irmag.auth_service.exceptions.UnexpectedErrorException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleInvalidRequestException_WhenInvalidRequest_ShouldReturnBadRequest() {
        // Arrange
        InvalidRequestException ex = new InvalidRequestException("Invalid request data");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidRequestException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request data", response.getBody().getMessage());
    }

    @Test
    void handleServerException_WhenServerErrorOccurs_ShouldReturnInternalServerError() {
        // Arrange
        ServerException ex = new ServerException("Server error occurred");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleServerException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Server error occurred", response.getBody().getMessage());
    }

    @Test
    void handleServiceUnavailableException_WhenServiceUnavailable_ShouldReturnServiceUnavailable() {
        // Arrange
        ServiceUnavailableException ex = new ServiceUnavailableException("Service is unavailable");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleServiceUnavailableException(ex);

        // Assert
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("Service is unavailable", response.getBody().getMessage());
    }

    @Test
    void handleUnexpectedErrorException_WhenUnexpectedErrorOccurs_ShouldReturnInternalServerError() {
        // Arrange
        UnexpectedErrorException ex = new UnexpectedErrorException("Unexpected error");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnexpectedErrorException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error", response.getBody().getMessage());
    }

    @Test
    void handleIllegalArgumentException_WhenInvalidArgument_ShouldReturnBadRequest() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        WebRequest request = mock(WebRequest.class);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request: Invalid argument", response.getBody().getMessage());
    }

    @Test
    void handleBadRequestException_WhenBadRequestOccurs_ShouldReturnBadRequest() {
        // Arrange
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad request",
                null,
                null,
                null
        );
        WebRequest request = mock(WebRequest.class);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadRequestException(
                (HttpClientErrorException.BadRequest) ex,
                request
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad Request: 400 Bad request", response.getBody().getMessage());
    }

    @Test
    void handleServerErrorException_WhenServerErrorOccurs_ShouldReturnInternalServerError() {
        // Arrange
        HttpServerErrorException ex = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error");
        WebRequest request = mock(WebRequest.class);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleServerErrorException(ex, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Server error: 500 Server error", response.getBody().getMessage());
    }

    @Test
    void handleResourceAccessException_WhenServiceUnavailable_ShouldReturnServiceUnavailable() {
        // Arrange
        ResourceAccessException ex = new ResourceAccessException("Service is down");
        WebRequest request = mock(WebRequest.class);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceAccessException(ex, request);

        // Assert
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("Service unavailable: Service is down", response.getBody().getMessage());
    }

    @Test
    void handleGlobalException_WhenUnhandledExceptionOccurs_ShouldReturnInternalServerError() {
        // Arrange
        Exception ex = new Exception("Global error occurred");
        WebRequest request = mock(WebRequest.class);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred: Global error occurred", response.getBody().getMessage());
    }
}
