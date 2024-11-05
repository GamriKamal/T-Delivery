package tdelivery.mr_irmag.route_service.domain.dto.courierCalculation;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourierServiceRequest {
    private Point courierCoordinates;
    private List<OrderForRouteDto> ordersForRoute;
}
