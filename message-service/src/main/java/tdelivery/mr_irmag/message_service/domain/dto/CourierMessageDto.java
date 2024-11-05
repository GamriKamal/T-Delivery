package tdelivery.mr_irmag.message_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourierMessageDto {
    private String timeOfDelivery;
    private String orderStatus;
    private String email;
    private String restaurantAddress;

}
