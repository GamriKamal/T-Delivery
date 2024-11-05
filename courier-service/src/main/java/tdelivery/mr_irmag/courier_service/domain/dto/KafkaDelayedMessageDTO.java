package tdelivery.mr_irmag.courier_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KafkaDelayedMessageDTO {
    private UUID orderId;
    private String orderStatus;
    private String email;
    private int time;
}
