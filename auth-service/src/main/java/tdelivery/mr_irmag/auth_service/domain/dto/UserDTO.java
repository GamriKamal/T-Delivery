package tdelivery.mr_irmag.auth_service.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tdelivery.mr_irmag.auth_service.domain.model.Role;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO для обработки запросов с user-service.")
public class UserDTO {

    @Schema(description = "Уникальный идентификатор пользователя.", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private UUID id;

    @NotBlank(message = "Username must not be empty")
    @Size(min = 2, max = 100, message = "Username must be between 2 and 100 characters")
    @Schema(description = "Имя пользователя. Должно содержать от 2 до 100 символов.", example = "john_doe", required = true)
    private String username;

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Email should be valid")
    @Schema(description = "Электронная почта пользователя. Должна быть валидной.", example = "john.doe@example.com", required = true)
    private String email;

    @NotNull(message = "Role must not be null")
    @Schema(description = "Роль пользователя в системе.", example = "ADMIN", required = true)
    private Role role;

}