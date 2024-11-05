package tdelivery.mr_irmag.order_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tdelivery.mr_irmag.order_service.domain.entity.Order;
import tdelivery.mr_irmag.order_service.domain.entity.OrderStatus;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderUpdateMessage {
    private UUID id;
    private OrderStatus status;
    private String message;

    public static OrderUpdateMessage of(Order order) {
        return OrderUpdateMessage.builder()
                .id(order.getId())
                .status(order.getStatus())
                .build();
    }
}
