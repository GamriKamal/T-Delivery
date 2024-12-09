package tdelivery.mr_irmag.courier_service.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import tdelivery.mr_irmag.courier_service.domain.dto.Point;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "Модель заказа")
public class Order {

    @NotNull(message = "Order ID cannot be null")
    @Schema(description = "Уникальный идентификатор заказа", example = "a0eeb410-75c6-11e9-8f9e-2a86e4085a59")
    private UUID id;

    @NotBlank(message = "Order name cannot be blank")
    @Size(max = 100, message = "Order name must be between {min} and {max} characters")
    @Schema(description = "Название заказа", example = "Пицца Маргарита")
    private String name;

    @NotNull(message = "Creation date cannot be null")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}", message = "Date must be in the format yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Дата создания заказа", example = "2024-12-03T12:00:00")
    private String createdDate;

    @NotBlank(message = "Delivery address cannot be blank")
    @Size(max = 200, message = "Delivery address must be less than {max} characters")
    @Schema(description = "Адрес доставки заказа", example = "ул. Ленина, д. 10, кв. 5")
    private String deliveryAddress;

    @Size(max = 500, message = "Comment cannot exceed {max} characters")
    @Schema(description = "Комментарий к заказу", example = "Без лука")
    private String comment;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Schema(description = "Email клиента", example = "customer@example.com")
    private String email;

    @NotNull(message = "Total amount cannot be null")
    @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
    @Schema(description = "Общая сумма заказа", example = "123.45")
    private Double totalAmount;

    @NotBlank(message = "Restaurant address cannot be blank")
    @Size(max = 200, message = "Restaurant address must be less than {max} characters")
    @Schema(description = "Адрес ресторана, откуда заказ", example = "ул. Пушкина, д. 20")
    private String restaurantAddress;

    @NotNull(message = "Location cannot be null")
    @Schema(description = "Географическое местоположение заказа", example = "POINT(37.6173 55.7558)")
    private Point location;

    @NotNull(message = "Time of delivery cannot be null")
    @Min(value = 1, message = "Time of delivery must be at least {value} minutes")
    @Max(value = 1440, message = "Time of delivery cannot exceed {value} minutes")
    @Schema(description = "Время доставки в минутах", example = "30")
    private Integer timeOfDelivery;

    @NotEmpty(message = "Order items cannot be empty")
    @Size(min = 1, message = "Order must contain at least one item")
    @Schema(description = "Список позиций в заказе", example = "[{\"itemId\":\"1\", \"name\":\"Пицца Маргарита\", \"quantity\":1}]")
    private List<OrderItem> items;

    public Order(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}




