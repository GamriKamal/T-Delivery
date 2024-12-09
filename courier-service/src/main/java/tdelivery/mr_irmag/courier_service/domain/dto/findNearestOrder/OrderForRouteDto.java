package tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import tdelivery.mr_irmag.courier_service.domain.dto.Point;
import tdelivery.mr_irmag.courier_service.domain.entity.Order;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "Ответ на запрос о ближайших заказах, отсортированных по линии, с использованием PostGIS")
public class OrderForRouteDto {

    @NotNull(message = "Order ID cannot be null")
    @Schema(description = "Идентификатор заказа", example = "f5f5f5f5-1111-1111-1111-111111111111")
    private UUID id;

    @NotNull(message = "Order location cannot be null")
    @Schema(description = "Географическая точка местоположения заказа", example = "POINT(37.6173 55.7558)")
    private Point orderLocation;

    public static List<OrderForRouteDto> from(List<Order> orders) {
        return orders.stream()
                .map(order -> OrderForRouteDto.builder()
                        .id(order.getId())
                        .orderLocation(order.getLocation())
                        .build())
                .collect(Collectors.toList());
    }
}

