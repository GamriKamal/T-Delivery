package tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tdelivery.mr_irmag.courier_service.domain.dto.Point;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NearestOrderRequestDto {

    @Min(1)
    @Max(7)
    private int radius = 3;

    @NotNull
    private Point point;
}
