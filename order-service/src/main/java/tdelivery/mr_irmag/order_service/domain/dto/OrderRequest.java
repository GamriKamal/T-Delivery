package tdelivery.mr_irmag.order_service.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    @NotBlank
    @Size(max = 100)
    String comment;

    @NotEmpty(message = "Order items cannot be empty")
    private List<@Valid OrderItemRequest> items;

    @NotBlank(message = "Address cannot be blank")
    private String address;
}
