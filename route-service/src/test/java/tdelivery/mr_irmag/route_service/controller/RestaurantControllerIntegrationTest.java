package tdelivery.mr_irmag.route_service.controller;

import static org.junit.jupiter.api.Assertions.*;


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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tdelivery.mr_irmag.route_service.TestContainerBase;
import tdelivery.mr_irmag.route_service.domain.entity.Address;
import tdelivery.mr_irmag.route_service.domain.entity.Restaurant;
import tdelivery.mr_irmag.route_service.repository.RestaurantRepository;
import tdelivery.mr_irmag.route_service.service.RestaurantService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class RestaurantControllerIntegrationTest extends TestContainerBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @BeforeEach
    void setUp() {
        restaurantRepository.deleteAll();
    }

    @Test
    void createRestaurant_ValidRestaurant_ShouldReturnCreatedRestaurant() throws Exception {
        // Arrange
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName("Example restaurant");
        restaurant.setAddress(new Address("Test Street", 40.7128, -74.0060));

        // Act
        ResultActions result = mockMvc.perform(post("/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(restaurant)));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantName").value("Example restaurant"))
                .andExpect(jsonPath("$.address.street").value("Test Street"));
    }

    @Test
    void getAllRestaurants_WhenRestaurantsExist_ShouldReturnRestaurantsList() throws Exception {
        // Arrange
        Restaurant restaurant1 = new Restaurant(UUID.randomUUID(),"Restaurant 1", new Address("Street 1", 40.0, -70.0));
        Restaurant restaurant2 = new Restaurant(UUID.randomUUID(),"Restaurant 2", new Address("Street 2", 41.0, -71.0));
        restaurantRepository.saveAll(List.of(restaurant1, restaurant2));

        // Act
        ResultActions result = mockMvc.perform(get("/restaurants").contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].restaurantName").value("Restaurant 1"))
                .andExpect(jsonPath("$[1].restaurantName").value("Restaurant 2"));
    }

    @Test
    void getRestaurantById_ValidId_ShouldReturnRestaurant() throws Exception {
        // Arrange
        Restaurant restaurant = new Restaurant(UUID.randomUUID(),"Restaurant Test", new Address("Test Address", 42.0, -72.0));
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        // Act
        ResultActions result = mockMvc.perform(get("/restaurants/" + savedRestaurant.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantName").value("Restaurant Test"))
                .andExpect(jsonPath("$.address.street").value("Test Address"));
    }

    @Test
    void updateRestaurant_ValidData_ShouldReturnUpdatedRestaurant() throws Exception {
        // Arrange
        Restaurant restaurant = new Restaurant(UUID.randomUUID(),"Original Restaurant", new Address("Original Address", 43.0, -73.0));
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        Restaurant updatedRestaurant = new Restaurant(UUID.randomUUID(),"Updated Restaurant", new Address("Updated Address", 44.0, -74.0));

        // Act
        ResultActions result = mockMvc.perform(put("/restaurants/" + savedRestaurant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updatedRestaurant)));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantName").value("Updated Restaurant"))
                .andExpect(jsonPath("$.address.street").value("Updated Address"));
    }

    @Test
    void deleteRestaurant_ValidId_ShouldReturnNoContent() throws Exception {
        // Arrange
        Restaurant restaurant = new Restaurant(UUID.randomUUID(), "Restaurant to Delete", new Address("Delete Address", 45.0, -75.0));
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        // Act
        ResultActions result = mockMvc.perform(delete("/restaurants/" + savedRestaurant.getId()));

        // Assert
        result.andExpect(status().isNoContent());
        assertTrue(restaurantRepository.findById(savedRestaurant.getId()).isEmpty());
    }
}