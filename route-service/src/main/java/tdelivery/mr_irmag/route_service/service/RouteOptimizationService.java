package tdelivery.mr_irmag.route_service.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery.GoogleDistanceMatrixResponse;
import tdelivery.mr_irmag.route_service.domain.dto.courierCalculation.CourierServiceRequest;
import tdelivery.mr_irmag.route_service.domain.dto.courierCalculation.OrderForRouteDto;
import tdelivery.mr_irmag.route_service.exception.OptimalRouteNotFound;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
public class RouteOptimizationService {

    private final GoogleApiService googleApiService;

    @Autowired
    public RouteOptimizationService(GoogleApiService googleApiService) {
        this.googleApiService = googleApiService;
    }

    public OrderForRouteDto findClosestOrder(CourierServiceRequest request) {
        List<CompletableFuture<GoogleDistanceMatrixResponse>> futures = request.getOrdersForRoute().stream()
                .map(order -> {
                    String url = googleApiService.buildDistanceMatrixUrl(request.getCourierCoordinates(), order.getOrderLocation());
                    return CompletableFuture.supplyAsync(() -> {
                        GoogleDistanceMatrixResponse response = googleApiService.getDurationOfDelivery(url);
                        response.setRestaurantName(String.valueOf(order.getId()));
                        response.setRestaurantCoordinates(order.getOrderLocation());
                        return response;
                    });
                })
                .toList();

        List<GoogleDistanceMatrixResponse> responses = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        GoogleDistanceMatrixResponse optimalOrder = findClosestOrder(responses);

        return OrderForRouteDto.builder()
                .id(UUID.fromString(optimalOrder.getRestaurantName()))
                .orderLocation(optimalOrder.getRestaurantCoordinates())
                .distance(optimalOrder.getDistance())
                .duration(optimalOrder.getDuration())
                .build();
    }

    private GoogleDistanceMatrixResponse findClosestOrder(List<GoogleDistanceMatrixResponse> responses) {
        return responses.stream()
                .min(Comparator.comparingInt(response -> response.getDuration().getValue()))
                .orElseThrow(() -> new OptimalRouteNotFound("There is no closest order"));
    }
}

