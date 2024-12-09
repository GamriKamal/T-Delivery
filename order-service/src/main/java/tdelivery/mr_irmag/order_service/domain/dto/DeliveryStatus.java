package tdelivery.mr_irmag.order_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tdelivery.mr_irmag.order_service.domain.entity.OrderStatus;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryStatus {
    private UUID orderId;
    private String message;
    private String imageUrl;
    private int progress;
    private boolean showMap;
    private OrderStatus orderStatus;

    private double courierLat;
    private double courierLng;
    private double restaurantLat;
    private double restaurantLng;
    private double userLat;
    private double userLng;
    private long timeToRestaurant;
    private long timeToUser;
}
