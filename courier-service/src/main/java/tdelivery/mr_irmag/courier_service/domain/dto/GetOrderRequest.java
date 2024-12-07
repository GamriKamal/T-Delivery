package tdelivery.mr_irmag.courier_service.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Запрос курьера на подтверждение принятия заказа")
public class GetOrderRequest {

    @Min(value = 1, message = "Radius must be at least {value}")
    @Max(value = 7, message = "Radius must be no more than {value}")
    @Builder.Default
    @Schema(description = "Радиус поиска (от 1 до 7 км)", example = "3")
    private int radius = 3;

    @NotNull(message = "Point cannot be null")
    @Schema(description = "Географическая точка местоположения курьера", example = "POINT(37.6173 55.7558)")
    private Point point;

    @NotNull(message = "Order ID cannot be null")
    @Schema(description = "Идентификатор заказа", example = "f5f5f5f5-1111-1111-1111-111111111111")
    private UUID orderId;
}

