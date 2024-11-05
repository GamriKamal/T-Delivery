package tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tdelivery.mr_irmag.courier_service.domain.dto.Point;
import tdelivery.mr_irmag.courier_service.domain.dto.RouteServiceResponse;
import tdelivery.mr_irmag.courier_service.domain.entity.Order;
import tdelivery.mr_irmag.courier_service.domain.entity.OrderItem;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetNearestOrderResponse {
    private String deliveryAddress;
    private String restaurantAddress;
    private String comment;
    private String email;
    private Double totalAmount;
    private String name;
    private Point orderLocation;
    private GoogleDistanceResponse distance;
    private GoogleDurationResponse duration;
    private List<OrderItem> items;

    public static GetNearestOrderResponse from(Order order, RouteServiceResponse response) {
        return GetNearestOrderResponse.builder()
                .deliveryAddress(order.getDeliveryAddress())
                .restaurantAddress(order.getRestaurantAddress())
                .comment(order.getComment())
                .email(order.getEmail())
                .totalAmount(order.getTotalAmount())
                .name(order.getName())
                .orderLocation(order.getLocation())
                .distance(response.getDistance())
                .duration(response.getDuration())
                .items(order.getItems())
                .build();
    }
}
