package tdelivery.mr_irmag.order_service.domain.dto.calculationDelivery;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Double totalPrice;
    private Double productPrice;
    private Double deliveryPrice;
    private String deliveryDuration;
    private String restaurantName;
    private String restaurantAddress;
    private Point restaurantCoordinates;

    public static CalculationDeliveryResponse toCalculationDeliveryResponse(RouteServiceResponse routeServiceResponse) {
        return CalculationDeliveryResponse.builder()
                .deliveryPrice(routeServiceResponse.getDeliveryPrice())
                .deliveryDuration(routeServiceResponse.getDeliveryDuration())
                .restaurantName(routeServiceResponse.getRestaurantName())
                .restaurantAddress(routeServiceResponse.getRestaurantAddress())
                .restaurantCoordinates(routeServiceResponse.getRestaurantCoordinates())
                .build();
    }
}