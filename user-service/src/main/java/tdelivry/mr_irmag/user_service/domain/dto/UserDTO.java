package tdelivry.mr_irmag.user_service.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tdelivry.mr_irmag.user_service.domain.entity.Role;
import tdelivry.mr_irmag.user_service.domain.entity.User;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private UUID id;

    @NotBlank(message = "Username must not be empty")
    @Size(min = 2, max = 100, message = "Username must be between 2 and 100 characters")
    private String username;

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password must not be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    private Role role;

    @Size(max = 255, message = "Address can be at most 255 characters")
    private String address;

    public static UserDTO of(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .address(user.getAddress())
                .build();
    }
}

