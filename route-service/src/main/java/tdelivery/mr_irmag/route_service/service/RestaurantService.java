package tdelivery.mr_irmag.route_service.service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import tdelivery.mr_irmag.route_service.domain.entity.Address;
import tdelivery.mr_irmag.route_service.domain.entity.Restaurant;
import tdelivery.mr_irmag.route_service.exception.InvalidAddressException;
import tdelivery.mr_irmag.route_service.exception.RestaurantAlreadyExistsException;
import tdelivery.mr_irmag.route_service.exception.RestaurantNotFoundException;
import tdelivery.mr_irmag.route_service.repository.RestaurantRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RestaurantService {

    private RestaurantRepository restaurantRepository;

    @Autowired
    public void setRestaurantRepository(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public Restaurant createRestaurant(@Valid Restaurant restaurant) {
        if (restaurantRepository.findByRestaurantName(restaurant.getRestaurantName()).isPresent()) {
            throw new RestaurantAlreadyExistsException(restaurant.getRestaurantName());
        }
        validateAddress(restaurant.getAddress());
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public Optional<Restaurant> getRestaurantById(UUID id) {
        return restaurantRepository.findById(id);
    }

    public Restaurant updateRestaurant(UUID id, @Valid Restaurant restaurantDetails) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));

        if (!restaurant.getRestaurantName().equals(restaurantDetails.getRestaurantName()) &&
                restaurantRepository.findByRestaurantName(restaurantDetails.getRestaurantName()).isPresent()) {
            throw new RestaurantAlreadyExistsException(restaurantDetails.getRestaurantName());
        }

        validateAddress(restaurantDetails.getAddress());

        restaurant.setRestaurantName(restaurantDetails.getRestaurantName());
        restaurant.setAddress(restaurantDetails.getAddress());

        return restaurantRepository.save(restaurant);
    }

    public void deleteRestaurant(UUID id) {
        restaurantRepository.deleteById(id);
    }

    private void validateAddress(Address address) {
        if (address == null) {
            throw new InvalidAddressException("Address must not be null.");
        }
        if (address.getStreet() == null || address.getStreet().isBlank()) {
            throw new InvalidAddressException("Street must not be empty.");
        }
        if (Double.isNaN(address.getX()) || Double.isNaN(address.getY())) {
            throw new InvalidAddressException("Coordinates must be valid numbers.");
        }
    }
}