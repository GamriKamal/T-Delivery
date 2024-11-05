package tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoogleDurationResponse {
    private String text;
    private int value;
}
