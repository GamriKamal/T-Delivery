package tdelivery.mr_irmag.auth_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Модель ответа, представляющая информацию об ошибке.")
public class ErrorResponse {
    @Schema(description = "Код ошибки, соответствующий возникшей проблеме.", example = "404")
    private int errorCode;

    @Schema(description = "Метка времени, когда произошла ошибка.", example = "2024-12-03T14:15:30")
    private LocalDateTime timestamp;

    @Schema(description = "Сообщение, описывающее ошибку.", example = "Resource not found")
    private String message;
}


