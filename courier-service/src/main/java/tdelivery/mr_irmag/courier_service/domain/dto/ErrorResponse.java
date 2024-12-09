package tdelivery.mr_irmag.courier_service.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Ответ на ошибку, содержащий код ошибки, метку времени и сообщение об ошибке")
public class ErrorResponse {
    @Schema(description = "Код ошибки HTTP", example = "404")
    private int errorCode;

    @Schema(description = "Метка времени, когда произошла ошибка", example = "2024-12-03T10:15:30")
    private LocalDateTime timestamp;

    @Schema(description = "Сообщение об ошибке", example = "Resource not found")
    private String message;
}


