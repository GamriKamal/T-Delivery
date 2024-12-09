package tdelivery.mr_irmag.order_service.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import tdelivery.mr_irmag.order_service.domain.dto.calculationDelivery.CalculateOrderItemRequest;
import tdelivery.mr_irmag.order_service.domain.dto.messageServiceDTO.MessageOrderItemDTO;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
public class OrderItem {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_id_seq")
    @SequenceGenerator(name = "order_id_seq", sequenceName = "order_id_seq", allocationSize = 1)
    @Schema(description = "Уникальный идентификатор товара в заказе", example = "1")
    private Long id;

    @NotBlank(message = "Название продукта не должно быть пустым")
    @Size(min = 2, max = 100, message = "Название продукта должно содержать от 2 до 100 символов")
    @Schema(description = "Название товара", example = "Пицца Маргарита")
    private String name;

    @Positive(message = "Цена должна быть положительным числом")
    @Schema(description = "Цена товара", example = "499.99")
    private Double price;

    @Positive(message = "Количество должно быть положительным числом")
    @Schema(description = "Количество товара", example = "2")
    private Integer quantity;

    @Size(max = 500, message = "Описание продукта не должно превышать 500 символов")
    @Schema(description = "Описание товара", example = "Вкусная пицца с сыром и томатами")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @Schema(description = "Заказ, к которому относится этот товар")
    private Order order;

    public static List<OrderItem> from(List<CalculateOrderItemRequest> items, Order order) {
        return items.stream()
                .map(item -> OrderItem.builder()
                        .name(item.getName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .description(item.getDescription())
                        .order(order)
                        .build())
                .collect(Collectors.toList());
    }

    public MessageOrderItemDTO toMessageOrderItemDTO() {
        return MessageOrderItemDTO.builder()
                .name(this.getName())
                .quantity(this.getQuantity())
                .price(this.getPrice())
                .description(this.getDescription())
                .build();
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", description='" + description + '\'' +
                ", orderId=" + (order != null ? order.getId() : "null") +
                '}';
    }
}

