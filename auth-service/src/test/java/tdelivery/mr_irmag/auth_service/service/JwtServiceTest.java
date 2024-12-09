package tdelivery.mr_irmag.auth_service.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import tdelivery.mr_irmag.auth_service.domain.model.Role;
import tdelivery.mr_irmag.auth_service.domain.model.User;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class JwtServiceTest {

    private final String jwtSigningKey = "bXlT3cQ7GQ9PTeRHz0kF8FlqKuoZQzP4Vbq4YlU1sjI=";
    @InjectMocks
    private JwtService jwtService;
    @Mock
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtService, "jwtSigningKey", jwtSigningKey);
    }


    @Test
    void extractUserName_validToken_shouldReturnUserName() {
        // Arrange
        String username = "testUser";
        String token = createTokenWithSubject(username);

        // Act
        String extractedUserName = jwtService.extractUserName(token);

        // Assert
        assertEquals(username, extractedUserName);
    }

    @Test
    void extractUserName_invalidToken_shouldThrowException() {
        // Arrange
        String invalidToken = "invalidToken";

        // Act & Assert
        assertThrows(Exception.class, () -> jwtService.extractUserName(invalidToken));
    }

    // generateToken tests

    @Test
    void generateToken_validUser_shouldReturnToken() {
        // Arrange
        when(user.getId()).thenReturn(UUID.randomUUID());
        when(user.getEmail()).thenReturn("ElenaAlvarado@gmail.com");
        when(user.getRole()).thenReturn(Role.USER);
        when(user.getUsername()).thenReturn("ElenaAlvarado");

        // Act
        String token = jwtService.generateToken(user);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    // isTokenValid tests

    @Test
    void isTokenValid_validTokenAndUser_shouldReturnTrue() {
        // Arrange
        String username = "ElenaAlvarado";
        when(user.getUsername()).thenReturn(username);
        String token = createTokenWithSubject(username);

        // Act
        boolean isValid = jwtService.isTokenValid(token, user);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_invalidUser_shouldReturnFalse() {
        // Arrange
        String token = createTokenWithSubject("NataliaChang");
        when(user.getUsername()).thenReturn("ElenaAlvarado");

        // Act
        boolean isValid = jwtService.isTokenValid(token, user);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_expiredToken_shouldReturnFalse() {
        // Arrange
        String token = createExpiredToken("ElenaAlvarado");
        when(user.getUsername()).thenReturn("ElenaAlvarado");

        // Act & Assert
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, user));
    }

    private String createTokenWithSubject(String subject) {
        Key signingKey = ReflectionTestUtils.invokeMethod(jwtService, "getSigningKey");
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private String createExpiredToken(String subject) {
        Key signingKey = ReflectionTestUtils.invokeMethod(jwtService, "getSigningKey");
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24))
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
