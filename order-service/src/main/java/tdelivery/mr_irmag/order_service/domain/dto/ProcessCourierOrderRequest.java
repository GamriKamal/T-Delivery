package tdelivery.mr_irmag.order_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;
import tdelivery.mr_irmag.order_service.domain.entity.OrderStatus;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProcessCourierOrderRequest {
    private UUID orderId;
    private OrderStatus orderStatus;
    private Point courierPoint;
    private Integer timeDelivery;
}
