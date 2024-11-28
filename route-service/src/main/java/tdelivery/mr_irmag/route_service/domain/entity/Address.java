package tdelivery.mr_irmag.route_service.domain.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private String street;
    @NotBlank
    private double x;
    @NotBlank
    private double y;
}
