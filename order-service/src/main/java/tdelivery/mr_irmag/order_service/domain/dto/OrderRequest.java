package tdelivery.mr_irmag.order_service.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    @NotBlank(message = "Customer email cannot be blank")
    @Email(message = "Invalid email format")
    private String customerEmail;

    @NotEmpty(message = "Order items cannot be empty")
    private List<@Valid OrderItemRequest> items;

    @NotBlank(message = "Address cannot be blank")
    private String address;
}
