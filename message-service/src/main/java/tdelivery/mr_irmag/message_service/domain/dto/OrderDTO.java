package tdelivery.mr_irmag.message_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    private String name;
    private String deliveryAddress;
    private String comment;
    private Double totalAmount;
    private List<OrderItemDTO> orderItems;
}
