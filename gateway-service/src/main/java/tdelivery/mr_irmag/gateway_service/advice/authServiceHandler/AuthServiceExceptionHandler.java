package tdelivery.mr_irmag.gateway_service.advice.authServiceHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import tdelivery.mr_irmag.gateway_service.dto.ErrorResponse;
import tdelivery.mr_irmag.gateway_service.exception.authServiceExcpetion.*;

import java.time.LocalDateTime;

@ControllerAdvice
public class AuthServiceExceptionHandler {

    private ErrorResponse buildErrorResponse(HttpStatus status, String message) {
        return ErrorResponse.builder()
                .errorCode(status.value())
                .timestamp(LocalDateTime.now())
                .message(message)
                .build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse response = buildErrorResponse(status, "Error: " + ex.getMessage());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse response = buildErrorResponse(status, ex.getMessage());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<ErrorResponse> handleServerException(ServerException ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse response = buildErrorResponse(status, ex.getMessage());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailableException(ServiceUnavailableException ex) {
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
        ErrorResponse response = buildErrorResponse(status, ex.getMessage());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(UnexpectedErrorException.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedErrorException(UnexpectedErrorException ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse response = buildErrorResponse(status, ex.getMessage());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse response = buildErrorResponse(status, "Invalid request: " + ex.getMessage());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(HttpClientErrorException.BadRequest ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse response = buildErrorResponse(status, "Bad Request: " + ex.getMessage());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleServerErrorException(HttpServerErrorException ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse response = buildErrorResponse(status, "Server error: " + ex.getMessage());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleResourceAccessException(ResourceAccessException ex, WebRequest request) {
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
        ErrorResponse response = buildErrorResponse(status, "Service unavailable: " + ex.getMessage());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse response = buildErrorResponse(status, "An error occurred: " + ex.getLocalizedMessage());
        return new ResponseEntity<>(response, status);
    }
}

