package tdelivry.mr_irmag.user_service.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserOrderDTO {
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String createdDate;
    private String deliveryAddress;
    private String comment;
    private Double totalAmount;
    private String status;
}
