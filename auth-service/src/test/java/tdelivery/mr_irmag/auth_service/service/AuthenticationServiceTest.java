package tdelivery.mr_irmag.auth_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tdelivery.mr_irmag.auth_service.domain.dto.JwtAuthenticationResponse;
import tdelivery.mr_irmag.auth_service.domain.dto.SignInRequest;
import tdelivery.mr_irmag.auth_service.domain.dto.SignUpRequest;
import tdelivery.mr_irmag.auth_service.domain.model.User;
import tdelivery.mr_irmag.auth_service.exceptions.FieldAlreadyExistsException;
import tdelivery.mr_irmag.auth_service.exceptions.UserNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class AuthenticationServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void signUp_validRequest_shouldReturnJwtAuthenticationResponse() {
        // Arrange
        SignUpRequest request = new SignUpRequest("PabloMiranda", "PabloMiranda@gmail.com", "LZ9q5AU1");
        User user = new User();
        when(userServiceClient.createUser(request)).thenReturn(user);
        String jwtToken = "test-jwt-token";
        when(jwtService.generateToken(user)).thenReturn(jwtToken);

        // Act
        JwtAuthenticationResponse response = authenticationService.signUp(request);

        // Assert
        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());
    }

    @Test
    void signUp_fieldAlreadyExistsException_shouldThrowConflictStatusException() {
        // Arrange
        SignUpRequest request = new SignUpRequest("PabloMiranda", "PabloMiranda@gmail.com", "LZ9q5AU1");
        when(userServiceClient.createUser(request)).thenThrow(new FieldAlreadyExistsException("Field exists"));

        // Act & Assert
        FieldAlreadyExistsException exception = assertThrows(FieldAlreadyExistsException.class, () -> {
            authenticationService.signUp(request);
        });
        assertTrue(exception.getMessage().contains("Field exists"));
    }

    @Test
    void signUp_unexpectedException_shouldThrowInternalServerErrorStatusException() {
        // Arrange
        SignUpRequest request = new SignUpRequest("PabloMiranda", "PabloMiranda@gmail.com", "LZ9q5AU1");
        when(userServiceClient.createUser(request)).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.signUp(request);
        });
        assertTrue(exception.getMessage().contains("An error occurred while signing up"));
    }


    @Test
    void signIn_validRequest_shouldReturnJwtAuthenticationResponse() {
        // Arrange
        SignInRequest request = new SignInRequest("PabloMiranda", "LZ9q5AU1");
        User user = new User();
        when(userServiceClient.getUserByUsername(request)).thenReturn(user);
        String jwtToken = "test-jwt-token";
        when(jwtService.generateToken(user)).thenReturn(jwtToken);

        // Act
        JwtAuthenticationResponse response = authenticationService.signIn(request);

        // Assert
        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());
    }

    @Test
    void signIn_userNotFound_shouldThrowNotFoundStatusException() {
        // Arrange
        SignInRequest request = new SignInRequest("PabloMiranda", "LZ9q5AU1");
        when(userServiceClient.getUserByUsername(request)).thenThrow(new UserNotFoundException("User not found"));

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            authenticationService.signIn(request);
        });
        assertTrue(exception.getMessage().contains("User not found"));
    }
}
