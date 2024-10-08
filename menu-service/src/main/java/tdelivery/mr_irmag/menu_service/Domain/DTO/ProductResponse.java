package tdelivery.mr_irmag.menu_service.Domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    @NotBlank(message = "Название продукта не должно быть пустым")
    @Size(min = 2, max = 100, message = "Название продукта должно содержать от 2 до 100 символов")
    @Schema(description = "Название продукта")
    private String name;

    @Positive(message = "Цена должна быть положительным числом")
    @Schema(description = "Цена продукта")
    private Double price;

    @Size(max = 500, message = "Описание продукта не должно превышать 500 символов")
    @Schema(description = "Описание продукта")
    private String description;

}
