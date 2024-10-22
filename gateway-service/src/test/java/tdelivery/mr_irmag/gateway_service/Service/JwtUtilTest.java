package tdelivery.mr_irmag.gateway_service.Service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.Key;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private String validSecret = "thisisaverylongsecretkeythatexceeds256bitlengthwhichisneededforhmacsha256";
    private String weakSecret = "shortKey";

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        setPrivateField(jwtUtil, "secret", validSecret);
        jwtUtil.init();
    }

    @Test
    void extractEmail_ValidToken_ShouldReturnEmail() throws Exception {
        // Arrange
        String token = Jwts.builder()
                .setClaims(Map.of("email", "test@example.com"))
                .signWith(getSigningKey(jwtUtil))
                .compact();

        // Act
        String result = jwtUtil.extractEmail(token);

        // Assert
        assertEquals("test@example.com", result);
    }

    @Test
    void extractRole_ValidToken_ShouldReturnRole() throws Exception {
        // Arrange
        String token = Jwts.builder()
                .setClaims(Map.of("role", "ADMIN"))
                .signWith(getSigningKey(jwtUtil))
                .compact();

        // Act
        String result = jwtUtil.extractRole(token);

        // Assert
        assertEquals("ADMIN", result);
    }

    @Test
    void extractRole_InvalidToken_ShouldThrowException() {
        // Arrange
        String invalidToken = "invalid_token";

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            jwtUtil.extractRole(invalidToken);
        });
    }

    @Test
    void isInvalid_ExpiredToken_ShouldReturnTrue() throws Exception {
        // Arrange
        String expiredToken = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(getSigningKey(jwtUtil))
                .compact();

        // Act & Assert
        assertThrows(ExpiredJwtException.class, () -> jwtUtil.isInvalid(expiredToken));

        }

    @Test
    void isInvalid_ValidToken_ShouldReturnFalse() throws Exception {
        // Arrange
        String validToken = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + 100000))
                .signWith(getSigningKey(jwtUtil))
                .compact();

        // Act
        boolean result = jwtUtil.isInvalid(validToken);

        // Assert
        assertFalse(result);
    }


    private void setPrivateField(Object targetObject, String fieldName, Object fieldValue) throws Exception {
        Field field = targetObject.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(targetObject, fieldValue);
    }

    private Key getSigningKey(Object targetObject) throws Exception {
        Method method = targetObject.getClass().getDeclaredMethod("getSigningKey");
        method.setAccessible(true);
        return (Key) method.invoke(targetObject);
    }
}

