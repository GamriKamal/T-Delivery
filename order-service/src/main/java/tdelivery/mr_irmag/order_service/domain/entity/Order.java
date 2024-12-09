package tdelivery.mr_irmag.order_service.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.properties.ArraySchema;
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
    @Schema(description = "Уникальный идентификатор заказа")
    private UUID id;

    @NotBlank(message = "Order name cannot be blank")
    @Column(name = "name", nullable = false)
    @Schema(description = "Название заказа", example = "Пицца Маргарита")
    private String name;

    @NotNull(message = "Created date cannot be null")
    @Column(name = "created_date", nullable = false)
    @Schema(description = "Дата и время создания заказа", example = "2024-12-07T15:30:00")
    private LocalDateTime createdDate;

    @NotBlank(message = "Delivery address cannot be blank")
    @Column(name = "delivery_address", nullable = false)
    @Schema(description = "Адрес доставки", example = "Москва, улица Ленина 10")
    private String deliveryAddress;

    @Column(columnDefinition = "geometry(Point,4326)", nullable = false)
    @Schema(description = "Координаты ресторана", example = "Point(37.6173 55.7558)")
    private Point restaurantCoordinates;

    @Column(columnDefinition = "geometry(Point,4326)", nullable = false)
    @Schema(description = "Координаты пользователя", example = "Point(37.6173 55.7558)")
    private Point userCoordinates;

    @Column(name = "comment")
    @Size(max = 500)
    @Schema(description = "Комментарий к заказу", example = "Пожалуйста, не добавляйте соль")
    private String comment;

    @NotNull(message = "Total amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than zero")
    @Column(name = "total_amount", nullable = false)
    @Schema(description = "Общая сумма заказа", example = "1200.50")
    private Double totalAmount;

    @Column(name = "restaurant_address", nullable = false)
    @Schema(description = "Адрес ресторана", example = "Москва, улица Горького 5")
    private String restaurantAddress;

    @Schema(description = "Время доставки (в минутах)", example = "30")
    private Integer timeOfDelivery;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Schema(description = "Статус заказа", example = "PAID")
    private OrderStatus status;

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Email should be valid")
    @Column(name = "email", nullable = false)
    @Schema(description = "Email пользователя", example = "user@example.com")
    private String email;

    @Column(name = "user_id", nullable = false)
    @Schema(description = "Идентификатор пользователя", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ArraySchema(schema = @Schema(implementation = OrderItem.class))
    @Schema(description = "Список товаров в заказе")
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
                .orderId(this.getId())
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
