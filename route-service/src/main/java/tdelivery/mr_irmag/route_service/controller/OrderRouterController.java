package tdelivery.mr_irmag.route_service.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery.CalculateOrderRequest;
import tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery.CalculationDeliveryResponse;
import tdelivery.mr_irmag.route_service.domain.dto.courierCalculation.CourierServiceRequest;
import tdelivery.mr_irmag.route_service.domain.dto.courierCalculation.OrderForRouteDto;
import tdelivery.mr_irmag.route_service.service.DistanceService;

import java.util.UUID;

@RestController
@RequestMapping("/delivery")
@Log4j2
public class OrderRouterController {
    private final DistanceService distanceService;

    @Autowired
    public OrderRouterController(DistanceService distanceService) {
        this.distanceService = distanceService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<CalculationDeliveryResponse> calculateDeliveryOrder(@RequestBody CalculateOrderRequest calculateOrderRequest){
        log.info("Calculating delivery order {}", calculateOrderRequest.toString());
        return new ResponseEntity<>(distanceService.calculateDelivery(calculateOrderRequest), HttpStatus.OK);
    }

    @PostMapping("/closestOrder")
    public ResponseEntity<OrderForRouteDto> findClosestOrder(@RequestBody CourierServiceRequest request){
        return new ResponseEntity<>(distanceService.findClosestCoordinates(request), HttpStatus.OK);
    }
}
