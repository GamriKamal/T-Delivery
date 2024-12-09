package tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalculateOrderRequest {
    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(max = 100, message = "Длина комментария не должна превышать 100 символов")
    private String comment;


    @NotEmpty(message = "Элементы заказа не могут быть пустыми")
    private List<@Valid CalculateOrderItemRequest> items;

    @NotBlank(message = "Адрес не может быть пустым")
    private String address;
}
