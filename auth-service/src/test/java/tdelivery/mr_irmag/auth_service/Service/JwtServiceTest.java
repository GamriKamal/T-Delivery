package tdelivery.mr_irmag.auth_service.Service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtService jwtService;

    private String validToken;
    private String expiredToken;

    private final String jwtSigningKey = "U2VjdXJlS2V5VGhhdElzVGhpc0xvbmdFbm91Z2hUb1N1cHBvcnRIRVNIZWxsbw=="; // 256-bit key

    @BeforeEach
    void setUp() throws Exception {
        Field jwtSigningKeyField = JwtService.class.getDeclaredField("jwtSigningKey");
        jwtSigningKeyField.setAccessible(true);
        jwtSigningKeyField.set(jwtService, jwtSigningKey);

        validToken = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 60000))  // 1 minute validity
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSigningKey)), SignatureAlgorithm.HS256)
                .compact();

        expiredToken = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 200000 * 60 * 24))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))  // Expired 1 second ago
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSigningKey)), SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void extractUserName_ValidToken_ShouldReturnCorrectUserName() {
        // Act
        String username = jwtService.extractUserName(validToken);

        // Assert
        assertEquals("testUser", username);
    }

    @Test
    void extractUserName_InvalidToken_ShouldThrowException() {
        // Arrange
        String invalidToken = "invalidToken";

        // Act & Assert
        assertThrows(Exception.class, () -> jwtService.extractUserName(invalidToken));
    }

    // 2. generateToken() tests
    @Test
    void generateToken_ValidUserDetails_ShouldGenerateToken() {
        // Act
        String token = jwtService.generateToken(userDetails);

        // Assert
        assertNotNull(token);
    }

    @Test
    void generateToken_NullUserDetails_ShouldThrowException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> jwtService.generateToken(null));
    }

    @Test
    void isTokenValid_ValidToken_ShouldReturnTrue() {
        when(userDetails.getUsername()).thenReturn("testUser");

        // Act
        boolean isValid = jwtService.isTokenValid(validToken, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ExpiredToken_ShouldReturnFalse() {
        // Act
//        boolean isValid = jwtService.isTokenValid(expiredToken, userDetails);
        // Act & Assert

        ExpiredJwtException exception = assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(expiredToken, userDetails));
        // Assert
    }

    @Test
    void isTokenValid_MismatchedUsername_ShouldReturnFalse() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("otherUser");

        // Act
        boolean isValid = jwtService.isTokenValid(validToken, userDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void generateToken_WeakKey_ShouldThrowWeakKeyException() throws Exception {
        // Arrange
        String weakSigningKey = "aShortKey";
        Field jwtSigningKeyField = JwtService.class.getDeclaredField("jwtSigningKey");
        jwtSigningKeyField.setAccessible(true);
        jwtSigningKeyField.set(jwtService, weakSigningKey);

        // Act & Assert
        assertThrows(WeakKeyException.class, () -> jwtService.generateToken(userDetails));
    }
}