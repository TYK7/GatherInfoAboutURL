package com.example.companyanalyzer.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    // TODO: Load these from application.properties
    @Value("${app.jwt.secret:DefaultSecretKeyWhichShouldBeLongAndSecureAndActuallyLoadedFromProperties}") // Default for now
    private String jwtSecretString;

    @Value("${app.jwt.expiration-ms:86400000}") // Default to 24 hours
    private int jwtExpirationInMs;

    private SecretKey jwtSecretKey;

    // Initialize key after properties are set
    @jakarta.annotation.PostConstruct
    public void init() {
        // Ensure the secret key is strong enough for HS256
        if (jwtSecretString.length() < 32) {
            logger.warn("JWT Secret is too short. Using a default secure key. PLEASE CONFIGURE a strong app.jwt.secret in properties.");
            this.jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Generates a secure key
        } else {
            this.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecretString.getBytes());
        }
    }

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        String roles = userPrincipal.getAuthorities().stream()
                         .map(GrantedAuthority::getAuthority)
                         .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }
}
