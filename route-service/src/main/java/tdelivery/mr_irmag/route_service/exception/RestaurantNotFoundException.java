package tdelivery.mr_irmag.route_service.exception;

import java.util.UUID;

public class RestaurantNotFoundException extends RuntimeException {
    public RestaurantNotFoundException(UUID id) {
        super("Restaurant not found with id " + id);
    }
}

