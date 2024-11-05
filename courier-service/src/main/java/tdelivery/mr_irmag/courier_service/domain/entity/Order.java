package tdelivery.mr_irmag.courier_service.domain.entity;

import jakarta.validation.constraints.*;
import lombok.*;
import tdelivery.mr_irmag.courier_service.domain.dto.Point;


import java.time.LocalDateTime;
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
    private List<OrderItem> items;

}



