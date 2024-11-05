package tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery;

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
    private Double deliveryPrice;
    private String deliveryDuration;
    private String restaurantName;
    private String restaurantAddress;
    private Point restaurantCoordinates;
}