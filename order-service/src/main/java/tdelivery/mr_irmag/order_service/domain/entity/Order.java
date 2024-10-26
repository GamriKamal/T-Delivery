package tdelivery.mr_irmag.order_service.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_id_seq")
    @SequenceGenerator(name = "order_id_seq", sequenceName = "order_id_seq", allocationSize = 1)
    private Long id;

    @NotBlank(message = "Order name cannot be blank")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Created date cannot be null")
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @NotBlank(message = "Delivery address cannot be blank")
    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "comment")
    @Size(max = 500)
    private String comment;

    @NotNull(message = "Total amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than zero")
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Email should be valid")
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

}



