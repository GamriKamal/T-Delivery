package tdelivery.mr_irmag.gateway_service.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${token.signing.key}")
    private String secret;

    private Key key;


    @PostConstruct
    public void init(){
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
    }

    public String extractRole(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role", String.class);

        } catch (SignatureException e) {
            System.err.println("Invalid JWT signature: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token " + e.getLocalizedMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("JWT token expired: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired " + e.getLocalizedMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Malformed token " + e.getLocalizedMessage());
        } catch (Exception e) {
            System.err.println("JWT parsing error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalid " + e.getLocalizedMessage());
        }
    }



    public boolean isInvalid(String token) {
        return this.isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers){
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        if (keyBytes.length < 32) {
            throw new WeakKeyException("The key is too weak! Must be at least 256 bits for HS256.");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
