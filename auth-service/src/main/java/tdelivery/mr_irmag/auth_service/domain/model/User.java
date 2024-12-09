package tdelivery.mr_irmag.auth_service.domain.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import tdelivery.mr_irmag.auth_service.domain.dto.UserDTO;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "Пользовательская сущность, представляющая информацию о пользователе.")
public class User {

    @NotNull(message = "User id must not be null")
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

    @NotBlank(message = "Password must not be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Schema(description = "Пароль пользователя. Минимум 8 символов.", example = "password123", required = true)
    private String password;

    @NotNull(message = "Role must not be null")
    @Schema(description = "Роль пользователя.", example = "ADMIN", required = true)
    private Role role;

    @Size(max = 255, message = "Address can be at most 255 characters")
    @Schema(description = "Адрес пользователя. Максимум 255 символов.", example = "123 Main St, Springfield")
    private String address;

    public static User of(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .role(userDTO.getRole())
                .build();
    }
}