package tdelivery.mr_irmag.order_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DistanceMatrixDTO {
    private DistanceDTO distance;
    private DurationDTO duration;
}
