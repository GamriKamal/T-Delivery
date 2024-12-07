package tdelivery.mr_irmag.auth_service.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
@ToString
@Schema(description = "DTO для передачи информации о пользователе в user-service.")
public class MessageDTO implements Serializable {

    @Schema(description = "Имя пользователя, передаваемое в user-service.", example = "john_doe", required = true)
    private String username;

    @Schema(description = "Электронная почта пользователя, передаваемая в user-service.", example = "john.doe@example.com", required = true)
    private String email;

    @JsonCreator
    public MessageDTO(
            @JsonProperty("username") String username,
            @JsonProperty("email") String email) {
        this.username = username;
        this.email = email;
    }
}