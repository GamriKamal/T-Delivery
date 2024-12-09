package tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Запрос курьера на поиск ближайших заказов")
public class NearestOrderRequestDto {

    @Min(value = 1, message = "Radius must be at least {value}")
    @Max(value = 7, message = "Radius must be no more than {value}")
    @Builder.Default
    @Schema(description = "Радиус поиска заказов (от 1 до 7 км)", example = "3")
    private int radius = 3;

    @NotNull(message = "Point cannot be null")
    @Schema(description = "Географическая точка, с которой начинается поиск заказов", example = "POINT(37.6173 55.7558)")
    private Point point;
}

