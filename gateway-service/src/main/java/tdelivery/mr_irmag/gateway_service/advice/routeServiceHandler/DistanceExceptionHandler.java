//package tdelivery.mr_irmag.gateway_service.advice.routeServiceHandler;
//
//import lombok.extern.log4j.Log4j2;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import tdelivery.mr_irmag.route_service.domain.dto.ErrorResponse;
//import tdelivery.mr_irmag.route_service.exception.GeocodingServiceException;
//import tdelivery.mr_irmag.route_service.exception.InvalidAddressException;
//import tdelivery.mr_irmag.route_service.exception.OptimalRouteNotFound;
//
//import java.time.LocalDateTime;
//
//@ControllerAdvice
//@Order(1)
//@Log4j2
//public class DistanceExceptionHandler {
//    @ExceptionHandler(GeocodingServiceException.class)
//    public ResponseEntity<ErrorResponse> handleGeocodingServiceException(GeocodingServiceException ex) {
//        log.error("Geocoding service error: {}", ex.getMessage());
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .errorCode(HttpStatus.SERVICE_UNAVAILABLE.value())
//                .timestamp(LocalDateTime.now())
//                .message(ex.getMessage())
//                .build();
//        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
//    }
//
//    @ExceptionHandler(InvalidAddressException.class)
//    public ResponseEntity<ErrorResponse> handleInvalidAddressException(InvalidAddressException ex) {
//        log.error("Invalid address provided: {}", ex.getMessage());
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .errorCode(HttpStatus.BAD_REQUEST.value())
//                .timestamp(LocalDateTime.now())
//                .message(ex.getMessage())
//                .build();
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(OptimalRouteNotFound.class)
//    public ResponseEntity<ErrorResponse> handleOptimalRouteNotFound(OptimalRouteNotFound ex) {
//        log.error("Optimal route not found: {}", ex.getMessage());
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .errorCode(HttpStatus.NOT_FOUND.value())
//                .timestamp(LocalDateTime.now())
//                .message(ex.getMessage())
//                .build();
//        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//    }
//}
