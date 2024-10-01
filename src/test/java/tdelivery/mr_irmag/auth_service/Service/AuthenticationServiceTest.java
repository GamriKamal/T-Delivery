package tdelivery.mr_irmag.auth_service.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import tdelivery.mr_irmag.auth_service.Domain.Model.User;
import tdelivery.mr_irmag.auth_service.Domain.Model.Role;
import tdelivery.mr_irmag.auth_service.Domain.DTO.SignInRequest;
import tdelivery.mr_irmag.auth_service.Domain.DTO.SignUpRequest;
import tdelivery.mr_irmag.auth_service.Domain.DTO.JwtAuthenticationResponse;
import tdelivery.mr_irmag.auth_service.Exceptions.EmailAlreadyExistsException;
import tdelivery.mr_irmag.auth_service.Exceptions.UsernameAlreadyExistsException;
import tdelivery.mr_irmag.auth_service.Repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private SignUpRequest signUpRequest;
    private SignInRequest signInRequest;
    private User user;

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequest("testUser", "test@example.com", "password123");
        signInRequest = new SignInRequest("testUser", "password123");

        user = User.builder()
                .username("testUser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    void signUp_ValidRequest_ShouldReturnJwtAuthenticationResponse() {
        // Arrange
        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(User.class))).thenReturn("testJwt");

        // Act
        JwtAuthenticationResponse response = authenticationService.signUp(signUpRequest);

        // Assert
        verify(userService).create(any(User.class));
        assertEquals("testJwt", response.getToken());
    }

    @Test
    void signIn_ValidCredentials_ShouldReturnJwtAuthenticationResponse() {
        // Arrange
        when(userService.userDetailsService()).thenReturn(new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return user;
            }
        });

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(user, null));

        when(jwtService.generateToken(user)).thenReturn("testJwt");

        // Act
        JwtAuthenticationResponse response = authenticationService.signIn(signInRequest);

        // Assert
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertEquals("testJwt", response.getToken());
    }

    @Test
    void signIn_InvalidCredentials_ShouldThrowAuthenticationException() {
        // Arrange
        when(userService.userDetailsService()).thenReturn(new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                throw new UsernameNotFoundException("User not found");
            }
        });

        // Act & Assert
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            authenticationService.signIn(signInRequest);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void signIn_UserDisabled_ShouldThrowAuthenticationException() {
        // Arrange

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new DisabledException("User is disabled"));

        // Act & Assert
        Exception exception = assertThrows(DisabledException.class, () -> {
            authenticationService.signIn(signInRequest);
        });
        assertEquals("User is disabled", exception.getMessage());
    }
}
