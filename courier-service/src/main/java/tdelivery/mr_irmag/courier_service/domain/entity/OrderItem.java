package tdelivery.mr_irmag.courier_service.domain.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private String name;
    private int quantity;
    private double price;
    private String description;
}
