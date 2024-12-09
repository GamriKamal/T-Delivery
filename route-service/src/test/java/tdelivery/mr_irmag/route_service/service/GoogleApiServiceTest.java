package tdelivery.mr_irmag.route_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery.GoogleDistanceMatrixResponse;
import tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery.GoogleDistanceResponse;
import tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery.GoogleDurationResponse;
import tdelivery.mr_irmag.route_service.domain.entity.Address;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleApiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GoogleApiService googleApiService;

    @Value("${google.geocode.url}")
    private String googleGeocodeApiUrl;

    @Value("${google.distanceMatrix.url}")
    private String googleDistanceMatrixApiKey;

    @Value("${google.googlemaps.api_key}")
    private String googleApiKey;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(googleApiService, "googleDistanceMatrixApiKey", "https://maps.googleapis.com/maps/api/distancematrix/json?");
        ReflectionTestUtils.setField(googleApiService, "googleApiKey", "mock-google-api-key");
    }

    @Test
    void getCoordinates_ValidResponse_ShouldReturnPoint() throws Exception {
        // Arrange
        String address = "123 Main St";
        String expectedUrl = googleGeocodeApiUrl + address;
        String mockResponse = "{ \"results\": [ { \"geometry\": { \"location\": { \"lat\": 40.7128, \"lng\": -74.0060 } } } ] }";
        JsonNode mockNode = new ObjectMapper().readTree(mockResponse);

        when(restTemplate.getForEntity(expectedUrl, String.class)).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
        when(objectMapper.readTree(anyString())).thenReturn(mockNode);

        // Act
        Point result = googleApiService.getCoordinates(address);

        // Assert
        assertNotNull(result);
        assertEquals(40.7128, result.getX());
        assertEquals(-74.0060, result.getY());
    }

    @Test
    void getCoordinates_InvalidResponse_ShouldThrowRuntimeException() throws Exception {
        // Arrange
        String address = "Invalid Address";
        String expectedUrl = googleGeocodeApiUrl + address;
        String invalidResponse = "{ \"error_message\": \"Invalid request\" }";

        when(restTemplate.getForEntity(expectedUrl, String.class)).thenReturn(new ResponseEntity<>(invalidResponse, HttpStatus.OK));
        when(objectMapper.readTree(anyString())).thenThrow(new RuntimeException("Error parsing response"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> googleApiService.getCoordinates(address));

        assertTrue(exception.getMessage().contains("Error getting coordinates"));
    }

    @Test
    void getDurationOfDelivery_InvalidResponse_ShouldThrowRuntimeException() throws JsonProcessingException {
        // Arrange
        String url = "mock-distance-matrix-url";
        String invalidResponse = "{ \"error_message\": \"Invalid request\" }";

        when(restTemplate.getForEntity(url, String.class)).thenReturn(new ResponseEntity<>(invalidResponse, HttpStatus.OK));
        when(objectMapper.readTree(anyString())).thenThrow(new RuntimeException("Error parsing response"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> googleApiService.getDurationOfDelivery(url));
        assertTrue(exception.getMessage().contains("Error getting delivery duration"));

    }

    @Test
    void buildDistanceMatrixUrl_ValidPoints_ShouldReturnCorrectUrl() {
        // Arrange
        Point origin = new Point(40.7128, -74.0060);
        Point destination = new Point(40.7130, -74.0055);

        // Act
        String result = googleApiService.buildDistanceMatrixUrl(origin, destination);

        // Assert
        String expected = "https://maps.googleapis.com/maps/api/distancematrix/json?" +
                "mode=driving&departureTime=now&origins=40.7128,-74.006&destinations=40.713,-74.0055&key=mock-google-api-key";
        assertEquals(expected, result);
    }

    @Test
    void toPoint_ValidAddress_ShouldReturnPoint() {
        // Arrange
        Address address = new Address("123 Main St", 40.7128, -74.0060);

        // Act
        Point result = GoogleApiService.toPoint(address);

        // Assert
        assertNotNull(result);
        assertEquals(40.7128, result.getX());
        assertEquals(-74.0060, result.getY());
    }
}
