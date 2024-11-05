package tdelivery.mr_irmag.order_service.domain.dto.messageServiceDTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MessageOrderItemDTO {
    private String name;
    private int quantity;
    private double price;
    private String description;
}
