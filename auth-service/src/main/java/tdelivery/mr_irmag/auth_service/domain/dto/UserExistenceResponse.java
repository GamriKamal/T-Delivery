package tdelivery.mr_irmag.auth_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserExistenceResponse {
    private boolean exists;
    private String message;
    private LocalDateTime timestamp;
}
