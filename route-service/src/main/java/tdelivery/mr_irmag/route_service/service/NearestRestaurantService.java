package tdelivery.mr_irmag.route_service.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import tdelivery.mr_irmag.route_service.config.DeliveryConfig;
import tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery.CalculateOrderRequest;
import tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery.CalculationDeliveryResponse;
import tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery.GoogleDistanceMatrixResponse;
import tdelivery.mr_irmag.route_service.exception.OptimalRouteNotFound;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
public class NearestRestaurantService {

    private final GoogleApiService googleApiService;
    private final DeliveryConfig deliveryConfig;
    private final RestaurantService restaurantService;

    @Autowired
    public NearestRestaurantService(GoogleApiService googleApiService, DeliveryConfig deliveryConfig, RestaurantService restaurantService) {
        this.googleApiService = googleApiService;
        this.deliveryConfig = deliveryConfig;
        this.restaurantService = restaurantService;
    }

    public CalculationDeliveryResponse calculateDelivery(CalculateOrderRequest request) {
        Point userPoint = googleApiService.getCoordinates(request.getAddress());
        GoogleDistanceMatrixResponse nearestRestaurant = findNearestRestaurant(userPoint);

        double total = deliveryConfig.getBasePrice() +
                (nearestRestaurant.getDistance().getValue() * deliveryConfig.getDistancePrice()) +
                (nearestRestaurant.getDuration().getValue() * deliveryConfig.getTimePrice());
        total = Math.round(total * 100.0) / 100.0;

        return CalculationDeliveryResponse.builder()
                .deliveryPrice(total)
                .deliveryDuration(nearestRestaurant.getDuration().getValue())
                .restaurantName(nearestRestaurant.getRestaurantName())
                .restaurantAddress(nearestRestaurant.getRestaurantAddress())
                .restaurantCoordinates(nearestRestaurant.getRestaurantCoordinates())
                .build();
    }

    private GoogleDistanceMatrixResponse findNearestRestaurant(Point userPoint) {
        var restaurants = restaurantService.getAllRestaurants();

        List<CompletableFuture<GoogleDistanceMatrixResponse>> futures = restaurants.stream()
                .map(restaurant -> {
                    String url = googleApiService.buildDistanceMatrixUrl(userPoint, GoogleApiService.toPoint(restaurant.getAddress()));
                    return CompletableFuture.supplyAsync(() -> {
                        GoogleDistanceMatrixResponse response = googleApiService.getDurationOfDelivery(url);
                        response.setRestaurantName(restaurant.getRestaurantName());
                        response.setRestaurantAddress(restaurant.getAddress().getStreet());
                        response.setRestaurantCoordinates(new Point(restaurant.getAddress().getX(), restaurant.getAddress().getY()));
                        return response;
                    });
                })
                .toList();

        List<GoogleDistanceMatrixResponse> responses = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        return findClosestRestaurant(responses);
    }

    private GoogleDistanceMatrixResponse findClosestRestaurant(List<GoogleDistanceMatrixResponse> responses) {
        return responses.stream()
                .min(Comparator.comparingInt(response -> response.getDuration().getValue()))
                .orElseThrow(() -> new OptimalRouteNotFound("There is no closest restaurant"));
    }
}

