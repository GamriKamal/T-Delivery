package tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalculationDeliveryResponse {
    @NotNull(message = "Стоимость доставки не может быть пустой")
    private Double deliveryPrice;

    @NotBlank(message = "Продолжительность доставки не может быть пустой")
    private Integer deliveryDuration;

    @NotBlank(message = "Название ресторана не может быть пустым")
    private String restaurantName;

    @NotBlank(message = "Адрес ресторана не может быть пустым")
    private String restaurantAddress;

    @NotNull(message = "Координаты ресторана не могут быть пустыми")
    private Point restaurantCoordinates;

    @NotNull(message = "Координаты пользователя не могут быть пустыми")
    private Point userPoint;
}