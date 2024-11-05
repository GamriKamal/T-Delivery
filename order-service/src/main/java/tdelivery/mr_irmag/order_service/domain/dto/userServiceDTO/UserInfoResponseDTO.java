package tdelivery.mr_irmag.order_service.domain.dto.userServiceDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoResponseDTO {
    private String username;
    private String email;
}
