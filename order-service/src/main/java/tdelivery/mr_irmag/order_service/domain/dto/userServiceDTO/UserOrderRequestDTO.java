package tdelivery.mr_irmag.order_service.domain.dto.userServiceDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tdelivery.mr_irmag.order_service.domain.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserOrderRequestDTO {
    private UUID orderId;
    private String name;
    private LocalDateTime createdDate;
    private String deliveryAddress;
    private String comment;
    private Double totalAmount;
    private OrderStatus status;
}
