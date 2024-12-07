package tdelivery.mr_irmag.courier_service.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Модель позиции в заказе")
public class OrderItem {

    @NotBlank(message = "Item name cannot be blank")
    @Size(max = 100, message = "Item name must be between {min} and {max} characters")
    @Schema(description = "Название позиции", example = "Пицца Маргарита")
    private String name;

    @Min(value = 1, message = "Quantity must be at least {value}")
    @Schema(description = "Количество товара", example = "2")
    private int quantity;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Schema(description = "Цена позиции", example = "500.00")
    private double price;

    @Size(max = 500, message = "Description cannot exceed {max} characters")
    @Schema(description = "Описание позиции", example = "Пицца с помидорами и моцареллой")
    private String description;
}

