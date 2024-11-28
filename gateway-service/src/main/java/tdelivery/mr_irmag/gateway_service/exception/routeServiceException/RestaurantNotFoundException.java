package tdelivery.mr_irmag.gateway_service.exception.routeServiceException;

import java.util.UUID;

public class RestaurantNotFoundException extends RuntimeException {
    public RestaurantNotFoundException(UUID id) {
        super("Restaurant not found with id " + id);
    }
}

