package tdelivery.mr_irmag.order_service.advise;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tdelivery.mr_irmag.order_service.domain.dto.ErrorResponse;
import tdelivery.mr_irmag.order_service.exception.UserServiceClientException;

import java.time.LocalDateTime;
import java.util.UUID;

@Log4j2
@ControllerAdvice
@Order(3)
public class GlobalExceptionHandler {

    @ExceptionHandler(UserServiceClientException.class)
    public ResponseEntity<ErrorResponse> handleUserServiceException(UserServiceClientException ex) {
        log.error("UserServiceClientException encountered for userId {}: {}", ex.getUserId(), ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(HttpStatus.SERVICE_UNAVAILABLE.value())
                .timestamp(LocalDateTime.now())
                .message(ex.getLocalizedMessage())
                .build();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }


}
