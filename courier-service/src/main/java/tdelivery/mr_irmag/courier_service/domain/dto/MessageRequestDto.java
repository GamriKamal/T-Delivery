package tdelivery.mr_irmag.courier_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tdelivery.mr_irmag.courier_service.domain.entity.OrderStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageRequestDto {
    private String timeOfDelivery;
    private String orderStatus;
    private String email;
    private String restaurantAddress;

    public String changeTime(){
        String[] parts = this.timeOfDelivery.split(" ");
        int originalMinutes = Integer.parseInt(parts[0]);

        int multipliedMinutes = originalMinutes * 3;

        return originalMinutes + "-" + multipliedMinutes + " минут";
    }

    public int getTime(){
        String[] parts = this.timeOfDelivery.split("-");
        return Integer.parseInt(parts[0].trim());
    }
}
