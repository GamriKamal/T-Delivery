package tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder;

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
