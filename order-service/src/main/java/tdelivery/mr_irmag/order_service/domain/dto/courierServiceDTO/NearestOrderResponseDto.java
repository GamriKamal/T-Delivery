package tdelivery.mr_irmag.order_service.domain.dto.courierServiceDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;
import tdelivery.mr_irmag.order_service.domain.entity.Order;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NearestOrderResponseDto {
    private UUID id;
    private String name;
    private String createdDate;
    private String deliveryAddress;
    private String comment;
    private String email;
    private Double totalAmount;
    private String restaurantAddress;
    private Point location;
    private List<NearestOrderItemResponseDto> items;

    public static List<NearestOrderResponseDto> from(List<Order> items) {
        return items.stream()
                .map(item -> NearestOrderResponseDto.builder()
                        .name(item.getName())
                        .totalAmount(item.getTotalAmount())
                        .comment(item.getComment())
                        .id(item.getId())
                        .createdDate(String.valueOf(item.getCreatedDate()))
                        .deliveryAddress(item.getDeliveryAddress())
                        .location(new Point(item.getPosition().getX(), item.getPosition().getY()))
                        .restaurantAddress(item.getRestaurantAddress())
                        .email(item.getEmail())
                        .items(NearestOrderItemResponseDto.from(item.getOrderItems()))
                        .build())
                .collect(Collectors.toList());
    }


}
