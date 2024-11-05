package tdelivery.mr_irmag.order_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tdelivery.mr_irmag.order_service.domain.dto.messageServiceDTO.MessageOrderDTO;
import tdelivery.mr_irmag.order_service.domain.entity.Order;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KafkaDelayedMessageDTO {
    private UUID orderId;
    private String email;
    private MessageOrderDTO order;
}
