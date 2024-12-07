package tdelivery.mr_irmag.route_service.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import tdelivery.mr_irmag.route_service.TestContainerBase;
import tdelivery.mr_irmag.route_service.domain.entity.Address;
import tdelivery.mr_irmag.route_service.domain.entity.Restaurant;
import tdelivery.mr_irmag.route_service.exception.RestaurantAlreadyExistsException;
import tdelivery.mr_irmag.route_service.exception.RestaurantNotFoundException;
import tdelivery.mr_irmag.route_service.repository.RestaurantRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = "eureka.client.enabled=false")
class RestaurantServiceTest{

    @Container
    private static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:13")
                    .withDatabaseName("routes_db")
                    .withUsername("postgres")
                    .withPassword("root")
                    .withExposedPorts(5432)
                    .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        postgreSQLContainer.start();
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @AfterAll
    public static void tearDown() {
        if (postgreSQLContainer != null) {
            postgreSQLContainer.stop();
        }
    }

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantService restaurantService;

    @Test
    void createRestaurant_RestaurantAlreadyExists_ShouldThrowException() {
        // Arrange
        Restaurant restaurant = restaurantService.getAllRestaurants().get(0);
        // Act & Assert
        assertThrows(RestaurantAlreadyExistsException.class, () -> restaurantService.createRestaurant(restaurant));
    }

    @Test
    void updateRestaurant_RestaurantNotFound_ShouldThrowException() {
        // Arrange
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = Restaurant.builder()
                .restaurantName("KFC на Алтынсарина")
                .address(Address.builder()
                                .street("qwrqwrqwr")
                                .x(43.22951184276599)
                                .y(76.85798410535114)
                                .build())
                .build();

        // Act & Assert
        assertThrows(RestaurantNotFoundException.class, () -> restaurantService.updateRestaurant(restaurantId, restaurant));
    }

    @Test
    void updateRestaurant_RestaurantAlreadyExistsException_ShouldThrowException() {
        // Arrange
        Restaurant restaurant = restaurantService.getAllRestaurants().get(0);

        // Act & Assert
        assertThrows(RestaurantAlreadyExistsException.class, () -> restaurantService.updateRestaurant(restaurant.getId(), restaurant));
    }
}