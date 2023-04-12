package ru.sstu.medtest.config.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.sstu.medtest.entity.UserEntity;

import java.util.Date;

@Component
public class JwtTokenUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        UserEntity userPrincipal = (UserEntity) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userPrincipal.getLogin())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            System.out.printf("Invalid JWT signature: %s \n", e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.printf("Invalid JWT token: %s \n", e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.printf("JWT token is expired: %s \n", e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.printf("JWT token is unsupported: %s \n", e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.printf("JWT claims string is empty: %s \n", e.getMessage());
        }
        return false;
    }
}
