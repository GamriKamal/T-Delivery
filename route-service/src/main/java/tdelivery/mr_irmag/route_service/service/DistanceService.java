package tdelivery.mr_irmag.route_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tdelivery.mr_irmag.route_service.config.DeliveryConfig;
import tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery.CalculateOrderRequest;
import tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery.CalculationDeliveryResponse;
import tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery.GoogleDistanceMatrixResponse;
import tdelivery.mr_irmag.route_service.domain.dto.courierCalculation.CourierServiceRequest;
import tdelivery.mr_irmag.route_service.domain.dto.courierCalculation.OrderForRouteDto;
import tdelivery.mr_irmag.route_service.domain.entity.Address;
import tdelivery.mr_irmag.route_service.exception.OptimalRouteNotFound;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
public class DistanceService {

    private final NearestRestaurantService nearestRestaurantService;
    private final RouteOptimizationService routeOptimizationService;

    @Autowired
    public DistanceService(NearestRestaurantService nearestRestaurantService, RouteOptimizationService routeOptimizationService) {
        this.nearestRestaurantService = nearestRestaurantService;
        this.routeOptimizationService = routeOptimizationService;
    }

    public CalculationDeliveryResponse calculateDelivery(CalculateOrderRequest request) {
        log.info("Address: {}", request.getAddress());
        return nearestRestaurantService.calculateDelivery(request);
    }

    public OrderForRouteDto findClosestCoordinates(CourierServiceRequest request) {
        return routeOptimizationService.findClosestOrder(request);
    }
}
