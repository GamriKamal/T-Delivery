package tdelivery.mr_irmag.courier_service.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tdelivery.mr_irmag.courier_service.domain.entity.OrderStatus;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO для отправки запроса в order-service для изменения состояния заказа на 'SHIPPED'")
public class ProcessCourierOrderRequest {

    @NotNull(message = "ID заказа не может быть пустым")
    @Schema(description = "Уникальный идентификатор заказа", example = "f9b7a798-5b3d-4560-9b6d-0c9c2c8a5b32")
    private UUID orderId;

    @NotNull(message = "Статус заказа не может быть пустым")
    @Schema(description = "Новый статус заказа", example = "SHIPPED")
    private OrderStatus orderStatus;

    @NotNull(message = "Координаты курьера не могут быть пустыми")
    @Schema(description = "Текущая географическая точка курьера", example = "{x: 55.7558, y: 37.6173}")
    private Point courierPoint;

    @Positive
    @Schema(description = "Время доставки в минутах", example = "30")
    private Integer timeDelivery;
}

