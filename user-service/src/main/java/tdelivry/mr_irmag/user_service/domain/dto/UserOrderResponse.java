package tdelivry.mr_irmag.user_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserOrderResponse {
    private String name;
    private LocalDateTime createdDate;
    private String deliveryAddress;
    private String comment;
    private Double totalAmount;
    private String status;
}


