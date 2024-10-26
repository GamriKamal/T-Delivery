package tdelivery.mr_irmag.message_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private String name;
    private int quantity;
    private double price;
}
