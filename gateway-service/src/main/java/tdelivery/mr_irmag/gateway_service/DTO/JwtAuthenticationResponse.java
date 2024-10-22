package tdelivery.mr_irmag.gateway_service.DTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class JwtAuthenticationResponse {
    private String token;
}
