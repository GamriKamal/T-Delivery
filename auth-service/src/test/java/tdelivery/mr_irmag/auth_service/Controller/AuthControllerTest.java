package tdelivery.mr_irmag.auth_service.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tdelivery.mr_irmag.auth_service.Domain.DTO.SignInRequest;
import tdelivery.mr_irmag.auth_service.Domain.DTO.SignUpRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void signUp_ValidRequest_ShouldReturnJwtResponse() throws Exception {
        // Arrange
        SignUpRequest signUpRequest = new SignUpRequest("test@example.com", "testUser", "password123");

        String jsonResult = "";
        // Act & Assert
        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonResult));
    }

    @Test
    void signIn_ValidRequest_ShouldReturnJwtResponse() throws Exception {
        // Arrange
        SignInRequest signInRequest = new SignInRequest("testUser", "password123");
        String jsonResult = "";

        // Act & Assert
        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonResult));
    }
}
