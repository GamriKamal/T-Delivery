package tdelivery.mr_irmag.route_service.domain.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Service
@Builder
@ToString
@Embeddable
public class Address {
    private String street;
    private double x;
    private double y;
}
