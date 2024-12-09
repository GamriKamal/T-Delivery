package tdelivery.mr_irmag.menu_service.domain.Entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    @Id
    @Schema(description = "Уникальный идентификатор продукта")
    private String id;

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

    @Pattern(regexp = "^https?://.*", message = "URL изображения должен начинаться с http:// или https://")
    @Schema(description = "URL изображения продукта")
    private String imageUrl;

    public Product(String name) {
        this.name = name;
    }
}
