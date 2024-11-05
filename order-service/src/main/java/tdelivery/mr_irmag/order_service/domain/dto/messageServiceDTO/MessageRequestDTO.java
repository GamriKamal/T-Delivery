package tdelivery.mr_irmag.order_service.domain.dto.messageServiceDTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MessageRequestDTO {
    private String statusOfOrder;
    private String email;
    private MessageOrderDTO order;
    private int timeOfCooking;
}
