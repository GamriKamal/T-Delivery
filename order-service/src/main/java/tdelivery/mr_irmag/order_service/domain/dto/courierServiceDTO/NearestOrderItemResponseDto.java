package tdelivery.mr_irmag.order_service.domain.dto.courierServiceDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tdelivery.mr_irmag.order_service.domain.entity.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NearestOrderItemResponseDto {
    private String name;
    private int quantity;
    private double price;
    private String description;

    public static List<NearestOrderItemResponseDto> from(List<OrderItem> items) {
        return items.stream()
                .map(item -> NearestOrderItemResponseDto.builder()
                        .name(item.getName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .description(item.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
