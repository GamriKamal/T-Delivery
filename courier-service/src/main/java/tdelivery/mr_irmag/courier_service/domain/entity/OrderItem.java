package tdelivery.mr_irmag.courier_service.domain.entity;

import lombok.*;


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
