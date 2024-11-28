package tdelivery.mr_irmag.route_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.geo.Point;
import tdelivery.mr_irmag.route_service.config.DeliveryConfig;
import tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery.*;
import tdelivery.mr_irmag.route_service.domain.entity.Address;
import tdelivery.mr_irmag.route_service.domain.entity.Restaurant;
import tdelivery.mr_irmag.route_service.exception.OptimalRouteNotFound;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NearestRestaurantServiceTest {

    @Mock
    private GoogleApiService googleApiService;

    @Mock
    private DeliveryConfig deliveryConfig;

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private NearestRestaurantService nearestRestaurantService;

    @Test
    void calculateDelivery_ValidRequest_ShouldReturnCalculationDeliveryResponse() {
        // Arrange
        CalculateOrderRequest request = new CalculateOrderRequest("comment",
                List.of(CalculateOrderItemRequest.builder()
                        .name("name")
                        .description("desc")
                        .price(100.0)
                        .quantity(2)
                        .build()),
                "123 Main St"
        );
        Point userPoint = new Point(40.7128, -74.0060);
        List<Restaurant> mockRestaurants = List.of(
                new Restaurant(UUID.randomUUID(), "Restaurant A", new Address("Street A", 40.7130, -74.0055)),
                new Restaurant(UUID.randomUUID(), "Restaurant B", new Address("Street B", 40.7140, -74.0045))
        );

        GoogleDistanceMatrixResponse nearestRestaurantResponseA = new GoogleDistanceMatrixResponse(
                new GoogleDistanceResponse("1 km", 1000),
                new GoogleDurationResponse("5 min", 600),
                "Restaurant A",
                "Street A",
                new Point(40.7130, -74.0055)
        );

        GoogleDistanceMatrixResponse nearestRestaurantResponseB = new GoogleDistanceMatrixResponse(
                new GoogleDistanceResponse("1.2 km", 1200),
                new GoogleDurationResponse("6 min", 720),
                "Restaurant B",
                "Street B",
                new Point(40.7140, -74.0045)
        );

        when(googleApiService.getCoordinates(request.getAddress())).thenReturn(userPoint);

        when(googleApiService.buildDistanceMatrixUrl(userPoint, new Point(40.7130, -74.0055)))
                .thenReturn("mock-url-restaurant-a");
        when(googleApiService.buildDistanceMatrixUrl(userPoint, new Point(40.7140, -74.0045)))
                .thenReturn("mock-url-restaurant-b");

        when(googleApiService.getDurationOfDelivery("mock-url-restaurant-a"))
                .thenReturn(nearestRestaurantResponseA);
        when(googleApiService.getDurationOfDelivery("mock-url-restaurant-b"))
                .thenReturn(nearestRestaurantResponseB);

        when(deliveryConfig.getBasePrice()).thenReturn(50.0);
        when(deliveryConfig.getDistancePrice()).thenReturn(0.1);
        when(deliveryConfig.getTimePrice()).thenReturn(0.2);

        when(restaurantService.getAllRestaurants()).thenReturn(mockRestaurants);

        // Act
        CalculationDeliveryResponse result = nearestRestaurantService.calculateDelivery(request);

        // Assert
        assertNotNull(result);
        assertEquals(50 + (1000 * 0.1) + (600 * 0.2), result.getDeliveryPrice());
        assertEquals("Restaurant A", result.getRestaurantName());
    }


    @Test
    void findClosestRestaurant_ValidResponses_ShouldReturnClosestRestaurant() {
        // Arrange
        GoogleDistanceMatrixResponse response1 = new GoogleDistanceMatrixResponse(
                new GoogleDistanceResponse("1.5 km", 1500),
                new GoogleDurationResponse("5 min", 900),
                "Restaurant A", "Street A", new Point(0, 0));
        GoogleDistanceMatrixResponse response2 = new GoogleDistanceMatrixResponse(
                new GoogleDistanceResponse("1.5 km", 1500),
                new GoogleDurationResponse("5 min", 900),
                "Restaurant B", "Street B", new Point(0, 0));
        List<GoogleDistanceMatrixResponse> responses = List.of(response1, response2);

        // Act
        GoogleDistanceMatrixResponse result = invokePrivateFindClosestRestaurant(responses);

        // Assert
        assertNotNull(result);
        assertEquals("Restaurant A", result.getRestaurantName());
    }

//    @Test
//    void findClosestRestaurant_EmptyResponses_ShouldThrowOptimalRouteNotFound() {
//        // Arrange
//        List<GoogleDistanceMatrixResponse> responses = Collections.emptyList();
//
//        // Act & Assert
//        OptimalRouteNotFound exception = assertThrows(OptimalRouteNotFound.class,
//                () -> invokePrivateFindClosestRestaurant(responses));
//        assertEquals("There is no closest restaurant", exception.getMessage());
//    }

    private GoogleDistanceMatrixResponse invokePrivateFindClosestRestaurant(List<GoogleDistanceMatrixResponse> responses) {
        try {
            Method method = NearestRestaurantService.class.getDeclaredMethod("findClosestRestaurant", List.class);
            method.setAccessible(true);
            return (GoogleDistanceMatrixResponse) method.invoke(nearestRestaurantService, responses);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
