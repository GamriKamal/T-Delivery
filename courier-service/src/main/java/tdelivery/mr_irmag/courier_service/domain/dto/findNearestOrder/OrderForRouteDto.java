package tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
public class OrderForRouteDto {
    private UUID id;
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
