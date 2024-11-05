package tdelivery.mr_irmag.courier_service.domain.dto;

import lombok.*;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.GoogleDistanceResponse;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.GoogleDurationResponse;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RouteServiceResponse {
    private UUID id;
    private Point orderLocation;
    private GoogleDistanceResponse distance;
    private GoogleDurationResponse duration;
}
