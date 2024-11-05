package tdelivery.mr_irmag.courier_service.domain.dto;

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
public class RouteServiceRequest {
    private Point courierCoordinates;
    private List<OrderForRouteDto> ordersForRoute;
}
