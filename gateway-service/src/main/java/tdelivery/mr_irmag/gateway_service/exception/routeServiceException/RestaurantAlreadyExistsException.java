package tdelivery.mr_irmag.gateway_service.exception.routeServiceException;

public class RestaurantAlreadyExistsException extends RuntimeException {
    public RestaurantAlreadyExistsException(String restaurantName) {
        super("Restaurant with name '" + restaurantName + "' already exists.");
    }
}
