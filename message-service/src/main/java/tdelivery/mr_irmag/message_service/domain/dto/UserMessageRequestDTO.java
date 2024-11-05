package tdelivery.mr_irmag.message_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserMessageRequestDTO {
    private String statusOfOrder;
    private String email;
    private OrderDTO order;
    private int timeOfCooking;
}

