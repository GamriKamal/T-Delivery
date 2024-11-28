package tdelivery.mr_irmag.courier_service.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetOrderRequest {

    @Min(1)
    @Max(7)
    @Builder.Default
    private int radius = 3;

    @NotNull
    private Point point;

    private UUID orderId;
}
