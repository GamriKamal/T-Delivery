package tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;
import tdelivery.mr_irmag.route_service.domain.entity.Address;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoogleDistanceMatrixResponse {
    @NotNull(message = "Информация о расстоянии не может быть пустой")
    private GoogleDistanceResponse distance;

    @NotNull(message = "Информация о продолжительности пути не может быть пустой")
    private GoogleDurationResponse duration;

    @NotBlank(message = "Название ресторана не может быть пустым")
    private String restaurantName;

    @NotBlank(message = "Адрес ресторана не может быть пустым")
    private String restaurantAddress;

    @NotNull(message = "Координаты ресторана не могут быть пустыми")
    private Point restaurantCoordinates;
}
