package tdelivery.mr_irmag.route_service.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @NotBlank(message = "Restaurant name must not be empty")
    @Size(min = 2, max = 100, message = "Restaurant name must be between 2 and 100 characters")
    @Column(name = "restaurant_name", nullable = false)
    private String restaurantName;

    @Embedded
    private Address address;
}
