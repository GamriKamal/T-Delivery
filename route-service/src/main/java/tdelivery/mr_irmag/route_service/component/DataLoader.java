package tdelivery.mr_irmag.route_service.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tdelivery.mr_irmag.route_service.domain.entity.Restaurant;
import tdelivery.mr_irmag.route_service.exception.RestaurantAlreadyExistsException;
import tdelivery.mr_irmag.route_service.service.RestaurantService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@Log4j2
public class DataLoader implements CommandLineRunner {

    private RestaurantService restaurantService;

    @Autowired
    public void setRestaurantService(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @Override
    public void run(String... args) throws Exception {
        loadRestaurants();
    }

    private void loadRestaurants() {
        List<Restaurant> restaurants = readRestaurantsFromJson();
        for (Restaurant restaurant : restaurants) {
            try {
                restaurantService.createRestaurant(restaurant);
            } catch (RestaurantAlreadyExistsException e) {
                log.warn("Restaurant already exists: {}", restaurant.getRestaurantName());
            }
        }
        log.info("Restaurants loaded!");
    }

    private List<Restaurant> readRestaurantsFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<List<Restaurant>> typeReference = new TypeReference<>() {};
        try (InputStream inputStream = TypeReference.class.getResourceAsStream("/restaurants.json")) {
            return objectMapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            log.error("Failed to load restaurants data", e);
            throw new RuntimeException("Failed to load restaurants data");
        }
    }
}

