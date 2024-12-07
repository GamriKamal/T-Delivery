package tdelivery.mr_irmag.courier_service.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.GoogleDistanceResponse;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.GoogleDurationResponse;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "DTO для ответа от сервиса маршрутов, содержащего информацию о заказе, расстоянии и времени доставки")
// Получаем значение из кэша. Кэш сделал в рамках курсовой, так как Google Maps Api не бесплатный
public class RouteServiceResponse {

    @Schema(description = "Идентификатор заказа", example = "f9b7a798-5b3d-4560-9b6d-0c9c2c8a5b32")
    private UUID id;

    @Schema(description = "Координаты местоположения заказа", example = "{x: 55.7558, y: 37.6173}")
    private Point orderLocation;

    @Schema(description = "Расстояние от курьера до заказа", implementation = GoogleDistanceResponse.class)
    private GoogleDistanceResponse distance;

    @Schema(description = "Время доставки до заказа", implementation = GoogleDurationResponse.class)
    private GoogleDurationResponse duration;
}
