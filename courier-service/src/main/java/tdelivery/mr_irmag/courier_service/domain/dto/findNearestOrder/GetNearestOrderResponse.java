package tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tdelivery.mr_irmag.courier_service.domain.dto.Point;
import tdelivery.mr_irmag.courier_service.domain.dto.RouteServiceResponse;
import tdelivery.mr_irmag.courier_service.domain.entity.Order;
import tdelivery.mr_irmag.courier_service.domain.entity.OrderItem;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Модель для ответа на запрос ближайших заказов")
public class GetNearestOrderResponse {

    @NotNull(message = "Order ID cannot be null")
    @Schema(description = "Идентификатор заказа", example = "a0eeb410-75c6-11e9-8f9e-2a86e4085a59")
    private UUID orderId;

    @NotBlank(message = "Delivery address cannot be blank")
    @Schema(description = "Адрес доставки", example = "ул. Ленина, д. 10, кв. 5")
    private String deliveryAddress;

    @NotBlank(message = "Restaurant address cannot be blank")
    @Schema(description = "Адрес ресторана", example = "ул. Пушкина, д. 20")
    private String restaurantAddress;

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

    @NotBlank(message = "Order name cannot be blank")
    @Schema(description = "Название заказа", example = "Пицца Маргарита")
    private String name;

    @NotNull(message = "Order location cannot be null")
    @Schema(description = "Географическое местоположение заказа", example = "POINT(37.6173 55.7558)")
    private Point orderLocation;

    @NotNull(message = "Distance cannot be null")
    @Schema(description = "Расстояние до заказа", implementation = GoogleDistanceResponse.class)
    private GoogleDistanceResponse distance;

    @NotNull(message = "Duration cannot be null")
    @Schema(description = "Время доставки", implementation = GoogleDurationResponse.class)
    private GoogleDurationResponse duration;

    @NotEmpty(message = "Order items cannot be empty")
    @Schema(description = "Список позиций в заказе", example = "[{\"itemId\":\"1\", \"name\":\"Пицца Маргарита\", \"quantity\":1}]")
    private List<OrderItem> items;

    public static List<GetNearestOrderResponse> from(List<Order> orders, RouteServiceResponse response) {
        return orders.stream()
                .map(order -> GetNearestOrderResponse.builder()
                        .orderId(order.getId())
                        .deliveryAddress(order.getDeliveryAddress())
                        .restaurantAddress(order.getRestaurantAddress())
                        .comment(order.getComment())
                        .email(order.getEmail())
                        .totalAmount(order.getTotalAmount())
                        .name(order.getName())
                        .orderLocation(order.getLocation())
                        .distance(response.getDistance())
                        .duration(response.getDuration())
                        .items(order.getItems())
                        .build())
                .collect(Collectors.toList());
    }
}
