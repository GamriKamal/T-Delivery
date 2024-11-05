package tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalculateOrderItemRequest {

    @NotBlank(message = "Product name must not be empty")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    @Positive(message = "Price must be a positive number")
    private Double price;

    @Positive(message = "Количество должно быть положительным числом")
    private Integer quantity;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
