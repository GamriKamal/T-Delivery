package tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "Модель для ответа с расстоянием от Google Maps")
public class GoogleDistanceResponse {

    @NotBlank(message = "Text cannot be blank")
    @Schema(description = "Текстовое описание расстояния (например, '10 км')", example = "10 km")
    private String text;

    @Min(value = 1, message = "Value must be at least {value}")
    @Schema(description = "Числовое значение расстояния в метрах", example = "10000")
    private int value;
}

