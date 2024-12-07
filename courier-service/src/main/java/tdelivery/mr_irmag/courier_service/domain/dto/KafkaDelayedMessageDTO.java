package tdelivery.mr_irmag.courier_service.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO для отправки отложенного сообщения через Kafka для уведомления в message-service")
public class KafkaDelayedMessageDTO {

    @NotNull(message = "Order ID cannot be null")
    @Schema(description = "Идентификатор заказа", example = "f5f5f5f5-1111-1111-1111-111111111111")
    private UUID orderId;

    @NotBlank(message = "Order status cannot be blank")
    @Schema(description = "Статус заказа", example = "PAID")
    private String orderStatus;

    @Email(message = "Email should be valid")
    @NotNull(message = "Email cannot be null")
    @Schema(description = "Email пользователя, для отправки уведомлений", example = "user@example.com")
    private String email;

    @Min(value = 1, message = "Time must be at least {value}")
    @Max(value = 1440, message = "Time must be no more than {value}")
    @Schema(description = "Время задержки уведомления в минутах", example = "30")
    private int time;
}

