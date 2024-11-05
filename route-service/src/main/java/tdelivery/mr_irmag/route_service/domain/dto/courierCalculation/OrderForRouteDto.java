package tdelivery.mr_irmag.route_service.domain.dto.courierCalculation;

import lombok.*;
import org.springframework.data.geo.Point;
import tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery.GoogleDistanceResponse;
import tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery.GoogleDurationResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderForRouteDto {
    private UUID id;
    private Point orderLocation;
    private GoogleDistanceResponse distance;
    private GoogleDurationResponse duration;

}
