package tdelivery.mr_irmag.route_service.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Embeddable
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Адрес, включая улицу и координаты")
public class Address {

    @NotBlank(message = "Улица не должна быть пустой")
    @Schema(description = "Улица, на которой расположен объект", example = "Красная площадь")
    private String street;

    @NotBlank(message = "Координата x не должна быть пустой")
    @Schema(description = "Координата X (широта)", example = "55.7558")
    private double x;

    @NotBlank(message = "Координата y не должна быть пустой")
    @Schema(description = "Координата Y (долгота)", example = "37.6176")
    private double y;
}
