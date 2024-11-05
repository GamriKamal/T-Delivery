package tdelivery.mr_irmag.route_service.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tdelivery.mr_irmag.route_service.domain.dto.ErrorResponse;
import tdelivery.mr_irmag.route_service.exception.InvalidAddressException;
import tdelivery.mr_irmag.route_service.exception.RestaurantAlreadyExistsException;
import tdelivery.mr_irmag.route_service.exception.RestaurantNotFoundException;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestaurantExceptionHandler {

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRestaurantNotFound(RestaurantNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(RestaurantAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleRestaurantAlreadyExists(RestaurantAlreadyExistsException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(HttpStatus.CONFLICT.value())
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(InvalidAddressException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAddress(InvalidAddressException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}

