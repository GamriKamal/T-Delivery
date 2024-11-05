package tdelivery.mr_irmag.route_service.exception;

public class RestaurantAlreadyExistsException extends RuntimeException {
    public RestaurantAlreadyExistsException(String restaurantName) {
        super("Restaurant with name '" + restaurantName + "' already exists.");
    }
}
