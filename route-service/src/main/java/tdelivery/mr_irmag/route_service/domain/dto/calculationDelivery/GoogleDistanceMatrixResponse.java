package tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery;

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
    private GoogleDistanceResponse distance;
    private GoogleDurationResponse duration;
    private String restaurantName;
    private String restaurantAddress;
    private Point restaurantCoordinates;
}
