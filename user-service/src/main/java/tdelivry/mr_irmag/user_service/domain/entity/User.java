package tdelivry.mr_irmag.user_service.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import tdelivry.mr_irmag.user_service.domain.dto.UserDTO;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@ToString
@Schema(description = "Пользователь системы")
public class User {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Schema(description = "Уникальный идентификатор пользователя", example = "b8d2bfa6-8fcb-11eb-8dcd-0242ac130003")
    private UUID id;

    @NotBlank(message = "Имя пользователя не должно быть пустым")
    @Size(min = 2, max = 100, message = "Имя пользователя должно содержать от 2 до 100 символов")
    @Column(name = "username", nullable = false)
    @Schema(description = "Имя пользователя", example = "johndoe")
    private String username;

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Email должен быть валидным")
    @Column(name = "email", nullable = false, unique = true)
    @Schema(description = "Электронная почта пользователя", example = "johndoe@example.com")
    private String email;

    @NotBlank(message = "Пароль не должен быть пустым")
    @Size(min = 8, message = "Пароль должен быть не менее 8 символов")
    @Column(name = "password", nullable = false)
    @Schema(description = "Пароль пользователя", example = "strongpassword123")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Schema(description = "Роль пользователя в системе", example = "ADMIN")
    private Role role;

    @Column(name = "address")
    @Schema(description = "Адрес пользователя", example = "Москва, ул. Ленина, д. 10")
    private String address;

    public static User of(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .role(userDTO.getRole())
                .address(userDTO.getAddress())
                .build();
    }
}