package tdelivery.mr_irmag.menu_service.Exception;

public class ProductAlreadyExistsException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Product already exists!";

    public ProductAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

    public ProductAlreadyExistsException(String message) {
        super(message);
    }
}
