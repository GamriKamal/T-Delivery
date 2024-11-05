package tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GoogleDistanceResponse {
    private String text;
    private int value;
}
