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
import tdelivery.mr_irmag.route_service.domain.entity.Restaurant;
import tdelivery.mr_irmag.route_service.exception.OptimalRouteNotFound;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
public class DistanceService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final DeliveryConfig deliveryConfig;
    private final RestaurantService restaurantService;

    @Value("${google.geocode.url}")
    private String googleGeocodeApiUrl;

    @Value("${google.distanceMatrix.url}")
    private String googleDistanceMatrixApiKey;

    @Value("${google.googlemaps.api_key}")
    private String GOOGLE_API_KEY;

    @Autowired
    public DistanceService(RestTemplate restTemplate, ObjectMapper objectMapper, DeliveryConfig deliveryConfig, RestaurantService restaurantService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.deliveryConfig = deliveryConfig;
        this.restaurantService = restaurantService;
    }

    private GoogleDistanceMatrixResponse findNearestRestaurant(Point userPoint){
        var list = restaurantService.getAllRestaurants();

        List<CompletableFuture<GoogleDistanceMatrixResponse>> futures = list.stream()
                .map(restaurant -> {
                    String url = buildDistanceMatrixUrl(userPoint, restaurant.getAddress());
                    return CompletableFuture.supplyAsync(() -> {
                        GoogleDistanceMatrixResponse response = getDurationOfDelivery(url);
                        response.setRestaurantName(restaurant.getRestaurantName());
                        response.setRestaurantAddress(restaurant.getAddress().getStreet());
                        log.info(restaurant.getAddress().toString());
                        response.setRestaurantCoordinates(new Point(restaurant.getAddress().getX(), restaurant.getAddress().getY()));
                        return response;
                    });
                })
                .toList();

        List<GoogleDistanceMatrixResponse> responses = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        var result = findClosestRestaurant(responses);
        if(result == null){
            throw new OptimalRouteNotFound("There is no closest restaurant");
        }

        log.info(result.getRestaurantCoordinates().toString());
        return result;
    }

    private GoogleDistanceMatrixResponse findClosestRestaurant(List<GoogleDistanceMatrixResponse> responses){
        Optional<GoogleDistanceMatrixResponse> nearest = responses.stream()
                .min(Comparator.comparingInt
                        (response -> response.getDuration().getValue()));

        return nearest.orElse(null);
    }

    public CalculationDeliveryResponse calculateDelivery(CalculateOrderRequest request) {
        log.info("Address: {}", request.getAddress());
        Point userPoint = getCoordinates(request.getAddress());
        GoogleDistanceMatrixResponse restaurant = findNearestRestaurant(userPoint);

        double total = deliveryConfig.getBasePrice() +
                (restaurant.getDistance().getValue() * deliveryConfig.getDistancePrice()) +
                (restaurant.getDuration().getValue() * deliveryConfig.getTimePrice());
        total = Math.round(total * 100.0) / 100.0;
        log.info("Total price: {}", total);

        return CalculationDeliveryResponse.builder()
                .deliveryPrice(total)
                .deliveryDuration(restaurant.getDuration().getText())
                .restaurantName(restaurant.getRestaurantName())
                .restaurantAddress(restaurant.getRestaurantAddress())
                .restaurantCoordinates(restaurant.getRestaurantCoordinates())
                .build();
    }

    public Point getCoordinates(String address) {
        String newUrl = googleGeocodeApiUrl + address;
        log.info("Google Geocode API URL: {}", newUrl);
        var rawResponse = restTemplate.getForEntity(newUrl, String.class).toString();
        String cleanedResponse = rawResponse.replaceAll("<", "").replaceAll(">", "").trim();
        cleanedResponse = cleanedResponse.replace("200 OK OK,", "");

        log.info(cleanedResponse + " yandex");
        try {
            JsonNode rootNode = objectMapper.readTree(cleanedResponse);

            JsonNode locationNode = rootNode.path("results").get(0).path("geometry").path("location");

            double latitude = locationNode.path("lat").asDouble();
            double longitude = locationNode.path("lng").asDouble();

            Point point = new Point(latitude, longitude);

            log.info(point.toString());

            return point;
        } catch (Exception e) {
            log.error("Error getting coordinates", e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    public GoogleDistanceMatrixResponse getDurationOfDelivery(String url) {
        log.info("Making request: {}", url);
        var rawResponse = restTemplate.getForEntity(url, String.class).toString();
        log.info("Result {}", rawResponse);
        String cleanedResponse = rawResponse.replaceAll("<", "").replaceAll(">", "").trim();
        cleanedResponse = cleanedResponse.replace("200 OK OK,", "");

        try {
            return objectMapper.readTree(cleanedResponse)
                    .path("rows")
                    .get(0)
                    .path("elements")
                    .get(0)
                    .traverse(objectMapper)
                    .readValueAs(GoogleDistanceMatrixResponse.class);
        } catch (Exception e) {
            log.error("Error getting coordinates", e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    private String buildDistanceMatrixUrl(Point userPoint, Address restaurantPoint) {
        var urlBuilder = new StringBuilder(googleDistanceMatrixApiKey);
        urlBuilder.append("mode=driving&");
        urlBuilder.append("departureTime=now&");
        urlBuilder.append("origins=").append(restaurantPoint.getX()).append(",").append(restaurantPoint.getY()).append("&");
        urlBuilder.append("destinations=").append(userPoint.getX()).append(",").append(userPoint.getY()).append("&");
        urlBuilder.append("&key=").append(GOOGLE_API_KEY);
        return urlBuilder.toString();
    }

    private String buildDistanceMatrixUrl(Point orderPoint, Point courierCoordinates) {
        var urlBuilder = new StringBuilder(googleDistanceMatrixApiKey);
        urlBuilder.append("mode=driving&");
        urlBuilder.append("departureTime=now&");
        urlBuilder.append("origins=").append(courierCoordinates.getX()).append(",").append(courierCoordinates.getY()).append("&");
        urlBuilder.append("destinations=").append(orderPoint.getX()).append(",").append(orderPoint.getY()).append("&");
        urlBuilder.append("&key=").append(GOOGLE_API_KEY);
        return urlBuilder.toString();
    }

    public OrderForRouteDto findClosestCoordinates(CourierServiceRequest request){
        List<CompletableFuture<GoogleDistanceMatrixResponse>> futures = request.getOrdersForRoute().stream()
                .map(order -> {
                    String url = buildDistanceMatrixUrl(request.getCourierCoordinates(), order.getOrderLocation());
                    return CompletableFuture.supplyAsync(() -> {
                        GoogleDistanceMatrixResponse response = getDurationOfDelivery(url);
                        response.setRestaurantName(String.valueOf(order.getId()));
                        response.setRestaurantCoordinates(order.getOrderLocation());
                        return response;
                    });
                })
                .toList();

        List<GoogleDistanceMatrixResponse> responses = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        GoogleDistanceMatrixResponse optimalOrder = findClosestRestaurant(responses);

        return OrderForRouteDto.builder()
                .id(UUID.fromString(optimalOrder.getRestaurantName()))
                .orderLocation(optimalOrder.getRestaurantCoordinates())
                .distance(optimalOrder.getDistance())
                .duration(optimalOrder.getDuration())
                .build();
    }

}
