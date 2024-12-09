package tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalculateOrderItemRequest {

    @NotBlank(message = "Название продукта не должно быть пустым")
    @Size(min = 2, max = 100, message = "Название продукта должно содержать от 2 до 100 символов")
    private String name;

    @Positive(message = "Цена должна быть положительным числом")
    private Double price;

    @Positive(message = "Количество должно быть положительным числом")
    private Integer quantity;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
