package tdelivery.mr_irmag.order_service.domain.dto.messageServiceDTO;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MessageOrderDTO {

    private String name;

    private String deliveryAddress;

    private String comment;

    private Double totalAmount;

    private List<MessageOrderItemDTO> orderItems;
}
