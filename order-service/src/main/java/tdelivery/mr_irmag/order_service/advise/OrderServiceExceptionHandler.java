package tdelivery.mr_irmag.order_service.advise;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tdelivery.mr_irmag.order_service.domain.dto.ErrorResponse;
import tdelivery.mr_irmag.order_service.exception.InvalidOrderStatusException;
import tdelivery.mr_irmag.order_service.exception.OrderNotFoundException;
import tdelivery.mr_irmag.order_service.exception.OrderProcessingException;
import tdelivery.mr_irmag.order_service.exception.UserServiceCommunicationException;

import java.time.LocalDateTime;

@Log4j2
@RestControllerAdvice
@Order(1)
public class OrderServiceExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderNotFoundException ex) {
        log.warn("Order not found: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .errorCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getLocalizedMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrderStatus(InvalidOrderStatusException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                400,
                LocalDateTime.now(),
                "Invalid order status: " + ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(OrderProcessingException.class)
    public ResponseEntity<ErrorResponse> handleOrderProcessingException(OrderProcessingException ex) {
        log.error("Order processing error: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getLocalizedMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UserServiceCommunicationException.class)
    public ResponseEntity<ErrorResponse> handleUserServiceCommunicationException(UserServiceCommunicationException ex) {
        log.error("User service communication error: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .errorCode(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message(ex.getLocalizedMessage())
                .build();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

}

