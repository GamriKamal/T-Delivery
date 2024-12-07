package tdelivery.mr_irmag.route_service.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.geo.Point;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tdelivery.mr_irmag.route_service.TestContainerBase;
import tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery.*;

import tdelivery.mr_irmag.route_service.domain.dto.courierCalculation.CourierServiceRequest;
import tdelivery.mr_irmag.route_service.domain.dto.courierCalculation.OrderForRouteDto;
import tdelivery.mr_irmag.route_service.domain.entity.Address;
import tdelivery.mr_irmag.route_service.domain.entity.Restaurant;
import tdelivery.mr_irmag.route_service.repository.RestaurantRepository;
import tdelivery.mr_irmag.route_service.service.DistanceService;
import tdelivery.mr_irmag.route_service.service.DistanceService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = "eureka.client.enabled=false")
class OrderRouterControllerIntegrationTest extends TestContainerBase {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DistanceService distanceService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void calculateDeliveryOrder_ValidRequest_ShouldReturnCalculationDeliveryResponse() throws Exception {
        // Arrange
        CalculateOrderRequest request = CalculateOrderRequest.builder()
                .address("123 Test Street, City")
                .comment("Deliver ASAP")
                .items(List.of(CalculateOrderItemRequest.builder()
                        .name("name")
                        .description("desc")
                        .price(100.0)
                        .quantity(2)
                        .build()))
                .build();

        CalculationDeliveryResponse response = CalculationDeliveryResponse.builder()
                .deliveryPrice(15.5)
                .deliveryDuration(30)
                .restaurantName("Test Restaurant")
                .restaurantAddress("123 Restaurant Street")
                .restaurantCoordinates(new Point(40.7128, -74.0060))
                .userPoint(new Point(41.1234, -73.9876))
                .build();

        when(distanceService.calculateDelivery(request)).thenReturn(response);

        // Act
        ResultActions result = mockMvc.perform(post("/delivery/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryPrice").value(15.5))
                .andExpect(jsonPath("$.deliveryDuration").value(30))
                .andExpect(jsonPath("$.restaurantName").value("Test Restaurant"))
                .andExpect(jsonPath("$.restaurantAddress").value("123 Restaurant Street"))
                .andExpect(jsonPath("$.restaurantCoordinates.x").value(40.7128))
                .andExpect(jsonPath("$.userPoint.x").value(41.1234));

        verify(distanceService).calculateDelivery(request);
    }

    @Test
    void findClosestOrder_ValidRequest_ShouldReturnOrderForRouteDto() throws Exception {
        // Arrange
        CourierServiceRequest request = CourierServiceRequest.builder()
                .courierCoordinates(new Point(41.1234, -73.9876))
                .ordersForRoute(List.of(
                        OrderForRouteDto.builder()
                                .id(UUID.randomUUID())
                                .orderLocation(new Point(40.7128, -74.0060))
                                .build()
                ))
                .build();

        OrderForRouteDto response = OrderForRouteDto.builder()
                .id(UUID.randomUUID())
                .orderLocation(new Point(40.7128, -74.0060))
                .distance(new GoogleDistanceResponse("5 km", 5000))
                .duration(new GoogleDurationResponse("10 min", 600))
                .build();

        when(distanceService.findClosestCoordinates(request)).thenReturn(response);

        // Act
        ResultActions result = mockMvc.perform(post("/delivery/closestOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.orderLocation.x").value(40.7128))
                .andExpect(jsonPath("$.distance.value").value(5000))
                .andExpect(jsonPath("$.duration.value").value(600));

        verify(distanceService).findClosestCoordinates(request);
    }
}

