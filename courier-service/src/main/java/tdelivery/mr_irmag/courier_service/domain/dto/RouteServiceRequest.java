package tdelivery.mr_irmag.courier_service.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.OrderForRouteDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO для запроса к route-service для нахождения ближайших заказов с учетом карты города")
public class RouteServiceRequest {

    @NotNull(message = "Координаты курьера не могут быть пустыми")
    @Schema(description = "Координаты текущего местоположения курьера", example = "{x: 55.7558, y: 37.6173}")
    private Point courierCoordinates;

    @Schema(description = "Список заказов, которые нужно учитывать для построения маршрута",
            example = "[{\"id\": \"f9b7a798-5b3d-4560-9b6d-0c9c2c8a5b32\", \"orderLocation\": {\"x\": 55.7558, \"y\": 37.6173}}]")
    private List<OrderForRouteDto> ordersForRoute;
}
