package tdelivery.mr_irmag.route_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tdelivery.mr_irmag.route_service.domain.entity.Restaurant;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
    Optional<Restaurant> findByRestaurantName(String restaurantName);
}

