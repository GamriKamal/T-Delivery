package tdelivery.mr_irmag.courier_service.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO для представления географической точки")
public class Point {
    @Schema(description = "Координата X (широта) точки", example = "55.7558")
    private double x;

    @Schema(description = "Координата Y (долгота) точки", example = "37.6173")
    private double y;
}

