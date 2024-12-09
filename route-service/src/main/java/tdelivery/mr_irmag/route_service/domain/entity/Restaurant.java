package tdelivery.mr_irmag.route_service.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Ресторан")
public class Restaurant {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Schema(description = "Идентификатор ресторана", example = "e29d1b4a-d506-4b61-8c93-5444c3d03959")
    private UUID id;

    @NotBlank(message = "Название ресторана не должно быть пустым")
    @Size(min = 2, max = 100, message = "Название ресторана должно быть от 2 до 100 символов")
    @Column(name = "restaurant_name", nullable = false)
    @Schema(description = "Название ресторана", example = "Ресторан №1")
    private String restaurantName;

    @Embedded
    @Schema(description = "Адрес ресторана")
    private Address address;
}
