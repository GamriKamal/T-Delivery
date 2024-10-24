package tdelivery.mr_irmag.auth_service.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
@ToString
public class MessageDTO implements Serializable {
    private String username;
    private String email;

    @JsonCreator
    public MessageDTO(@JsonProperty("username") String username, @JsonProperty("email") String email) {
        this.username = username;
        this.email = email;
    }
}
