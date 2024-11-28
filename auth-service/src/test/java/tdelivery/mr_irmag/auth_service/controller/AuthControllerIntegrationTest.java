package tdelivery.mr_irmag.auth_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tdelivery.mr_irmag.auth_service.domain.dto.JwtAuthenticationResponse;
import tdelivery.mr_irmag.auth_service.domain.dto.SignInRequest;
import tdelivery.mr_irmag.auth_service.domain.dto.SignUpRequest;
import tdelivery.mr_irmag.auth_service.exceptions.FieldAlreadyExistsException;
import tdelivery.mr_irmag.auth_service.exceptions.UserNotFoundException;
import tdelivery.mr_irmag.auth_service.service.AuthenticationService;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void signUp_ValidRequest_ShouldReturnJwtToken() throws Exception {
        // Arrange
        SignUpRequest signUpRequest = new SignUpRequest("username", "user@example.com", "password");
        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse("mockJwtToken");

        when(authenticationService.signUp(signUpRequest)).thenReturn(jwtResponse);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-up")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("mockJwtToken"));
    }

    @Test
    public void signIn_ValidRequest_ShouldReturnJwtToken() throws Exception {
        // Arrange
        SignInRequest signInRequest = new SignInRequest("username", "password");
        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse("mockJwtToken");

        when(authenticationService.signIn(signInRequest)).thenReturn(jwtResponse);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-in")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("mockJwtToken"));
    }

    @Test
    public void signUp_UsernameAlreadyExists_ShouldReturnConflict() throws Exception {
        // Arrange
        SignUpRequest signUpRequest = new SignUpRequest("username", "user@example.com", "password");

        when(authenticationService.signUp(signUpRequest)).thenThrow(new FieldAlreadyExistsException("Field already exists"));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-up")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void signIn_UserNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        SignInRequest signInRequest = new SignInRequest("username", "password");

        when(authenticationService.signIn(signInRequest)).thenThrow(new UserNotFoundException("User not found"));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-in")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
