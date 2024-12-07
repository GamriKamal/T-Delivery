package tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Модель для ответа с длительностью поездки от Google Maps")
public class GoogleDurationResponse {

    @NotBlank(message = "Text cannot be blank")
    @Schema(description = "Текстовое описание времени поездки (например, '15 мин')", example = "15 min")
    private String text;

    @Min(value = 1, message = "Value must be at least {value}")
    @Schema(description = "Числовое значение времени в секундах", example = "900")
    private int value;
}

