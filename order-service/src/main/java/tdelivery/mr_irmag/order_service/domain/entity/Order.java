package tdelivery.mr_irmag.order_service.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import tdelivery.mr_irmag.order_service.domain.dto.calculationDelivery.CalculateOrderRequest;
import tdelivery.mr_irmag.order_service.domain.dto.userServiceDTO.UserInfoResponseDTO;
import tdelivery.mr_irmag.order_service.domain.dto.userServiceDTO.UserOrderRequestDTO;
import tdelivery.mr_irmag.order_service.domain.dto.calculationDelivery.*;
import tdelivery.mr_irmag.order_service.domain.dto.messageServiceDTO.MessageOrderDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@ToString
public class Order {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @NotBlank(message = "Order name cannot be blank")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Created date cannot be null")
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @NotBlank(message = "Delivery address cannot be blank")
    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(columnDefinition = "geometry(Point,4326)", nullable = false)
    private Point restaurantCoordinates;

    @Column(columnDefinition = "geometry(Point,4326)", nullable = false)
    private Point userCoordinates;

    private static final GeometryFactory geometryFactory = new GeometryFactory();

    @Column(name = "comment")
    @Size(max = 500)
    private String comment;

    @NotNull(message = "Total amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than zero")
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "restaurant_address", nullable = false)
    private String restaurantAddress;

    private Integer timeOfDelivery;

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

    public Order(OrderStatus status) {
        this.status = status;
    }

    public static Order from(UUID userId, CalculateOrderRequest request, UserInfoResponseDTO userDTO,
                             Double totalAmount, org.springframework.data.geo.Point restaurantCoordinates,
                             org.springframework.data.geo.Point userCoordinates, String restaurantAddress, Integer timeOfDelivery) {
        Point restaurantPosition = geometryFactory.createPoint(new Coordinate(
                restaurantCoordinates.getX(),
                restaurantCoordinates.getY()
        ));

        Point userPosition = geometryFactory.createPoint(new Coordinate(
                userCoordinates.getX(),
                userCoordinates.getY()
        ));


        return Order.builder()
                .name(request.getItems().stream()
                        .map(CalculateOrderItemRequest::getName)
                        .collect(Collectors.joining(", ")))
                .createdDate(LocalDateTime.now())
                .deliveryAddress(request.getAddress())
                .comment(request.getComment())
                .totalAmount(totalAmount)
                .status(OrderStatus.PAID)
                .userId(userId)
                .email(userDTO.getEmail())
                .restaurantCoordinates(restaurantPosition)
                .userCoordinates(userPosition)
                .restaurantAddress(restaurantAddress)
                .timeOfDelivery(timeOfDelivery)
                .build();
    }

    public MessageOrderDTO toMessageOrderDTO() {
        return MessageOrderDTO.builder()
                .name(this.getName())
                .deliveryAddress(this.getDeliveryAddress())
                .comment(this.getComment())
                .totalAmount(this.getTotalAmount())
                .orderItems(this.getOrderItems().stream()
                        .map(OrderItem::toMessageOrderItemDTO)
                        .toList())
                .build();
    }

    public UserOrderRequestDTO toUserOrderRequestDTO() {
        return UserOrderRequestDTO.builder()
                .name(this.getName())
                .createdDate(this.getCreatedDate())
                .deliveryAddress(this.getDeliveryAddress())
                .comment(this.getComment())
                .totalAmount(this.getTotalAmount())
                .status(this.getStatus())
                .build();

    }

    public void setRestaurantCoordinates(double x, double y) {
        this.restaurantCoordinates = geometryFactory.createPoint(new Coordinate(x, y));
    }

    public void setUserCoordinates(double x, double y) {
        this.userCoordinates = geometryFactory.createPoint(new Coordinate(x, y));
    }
}
