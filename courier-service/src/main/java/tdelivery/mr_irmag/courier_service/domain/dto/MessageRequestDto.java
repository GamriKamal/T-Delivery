package tdelivery.mr_irmag.courier_service.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO для отправки сообщения в message-service")
public class MessageRequestDto {

    @NotNull(message = "Time of delivery cannot be null")
    @Pattern(regexp = "^\\d+\\s[м|М]ин$", message = "Time of delivery must be in the format 'X мин', where X is a number")
    @Schema(description = "Время доставки в минутах, например '30 мин'", example = "30 мин")
    private String timeOfDelivery;

    @NotBlank(message = "Order status cannot be blank")
    @Schema(description = "Статус заказа", example = "PAID")
    private String orderStatus;

    @Email(message = "Email should be valid")
    @NotNull(message = "Email cannot be null")
    @Schema(description = "Email пользователя, для отправки уведомлений", example = "user@example.com")
    private String email;

    @NotBlank(message = "Restaurant address cannot be blank")
    @Schema(description = "Адрес ресторана", example = "ул. Пушкина, д. 10")
    private String restaurantAddress;

    /**
     * Метод для изменения времени доставки
     * Умножает исходное количество минут на 3 и возвращает строку в формате "X-X минут"
     * @return измененное время доставки
     */
    public String changeTime() {
        String[] parts = this.timeOfDelivery.split(" ");
        int originalMinutes = Integer.parseInt(parts[0]);

        int multipliedMinutes = originalMinutes * 3;

        return originalMinutes + "-" + multipliedMinutes + " минут";
    }
}
