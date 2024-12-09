package tdelivery.mr_irmag.order_service.domain.dto.courierServiceDTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NearestOrderRequestDto {

    @Min(1)
    @Max(7)
    @Builder.Default
    private int radius = 3;

    @NotNull
    private Point point;
}
