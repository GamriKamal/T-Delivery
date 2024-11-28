package tdelivery.mr_irmag.courier_service.domain.entity;

import lombok.*;
import tdelivery.mr_irmag.courier_service.domain.dto.Point;

import java.util.List;
import java.util.UUID;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {
    private UUID id;
    private String name;
    private String createdDate;
    private String deliveryAddress;
    private String comment;
    private String email;
    private Double totalAmount;
    private String restaurantAddress;
    private Point location;
    private Integer timeOfDelivery;
    private List<OrderItem> items;

    public Order(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}



