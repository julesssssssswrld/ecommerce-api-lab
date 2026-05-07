package com.ws101.tomacas.EcommerceApi.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility service for JWT (JSON Web Token) operations.
 *
 * <p>Handles the generation, extraction, and validation of JWT tokens,
 * utilizing a secret key for signing and verifying signatures.
 * The secret and expiration are injected from {@code application.properties}.</p>
 *
 * @author Jules Ian C. Tomacas
 */
@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME; // in milliseconds, e.g., 86400000 for 24 hours

    /**
     * Extracts the username (subject) from the JWT token.
     *
     * @param token the JWT token string
     * @return the username stored in the token's subject claim
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the token using a resolver function.
     *
     * @param token          the JWT token string
     * @param claimsResolver a function that extracts the desired claim
     * @param <T>            the type of the claim value
     * @return the extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Validates the token against the provided user details.
     * Checks if the token is expired and if the username matches.
     *
     * @param token       the JWT token string
     * @param userDetails the user details to validate against
     * @return {@code true} if the token is valid for this user
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Generates a new JWT token for the given user details.
     *
     * @param userDetails the authenticated user's details
     * @return the signed JWT token string
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a token with additional claims beyond the default subject.
     *
     * <p>The token includes the username as the subject, the issued-at
     * timestamp, and an expiration based on the configured duration.</p>
     *
     * @param extraClaims additional claims to include in the token payload
     * @param userDetails the authenticated user's details
     * @return the signed JWT token string
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Checks if the token has expired.
     *
     * @param token the JWT token string
     * @return {@code true} if the token's expiration is before the current time
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the token.
     *
     * @param token the JWT token string
     * @return the expiration {@link Date}
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Parses all claims from the token.
     * Throws an exception if the signature is invalid or the token is malformed.
     *
     * @param token the JWT token string
     * @return the {@link Claims} contained in the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Derives the {@link SecretKey} from the configured Base64-encoded secret string.
     * Ensures the key is at least 256 bits (32 bytes) for HS256.
     *
     * @return the HMAC-SHA signing key
     * @throws IllegalArgumentException if the secret is too short
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                    "Secret key must be at least 32 bytes (256 bits) for HS256");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
