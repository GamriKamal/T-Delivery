package tdelivery.mr_irmag.order_service.advise;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tdelivery.mr_irmag.order_service.domain.dto.ErrorResponse;
import tdelivery.mr_irmag.order_service.exception.KafkaException;

import java.time.LocalDateTime;

@Log4j2
@ControllerAdvice
@Order(2)
public class KafkaExceptionHandler {

    @ExceptionHandler(KafkaException.class)
    public ResponseEntity<ErrorResponse> handleKafkaException(KafkaException ex) {
        log.error("Kafka error: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getLocalizedMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}

