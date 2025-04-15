package com.bookstore.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }
    
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }
    
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }
    
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    private Claims extractAllClaims(String token) {
        try {
            System.out.println("Attempting to parse JWT token: " + token.substring(0, Math.min(10, token.length())) + "...");
            Claims claims = Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("Successfully parsed JWT token");
            return claims;
        } catch (ExpiredJwtException e) {
            System.err.println("JWT token expired: " + e.getMessage());
            // Return empty claims with expiration information
            Claims emptyClaims = Jwts.claims();
            emptyClaims.put("error", "Token expired");
            return emptyClaims;
        } catch (MalformedJwtException e) {
            System.err.println("Malformed JWT token: " + e.getMessage());
            // Return empty claims with error information
            Claims emptyClaims = Jwts.claims();
            emptyClaims.put("error", "Malformed token");
            return emptyClaims;
        } catch (UnsupportedJwtException e) {
            System.err.println("Unsupported JWT token: " + e.getMessage());
            Claims emptyClaims = Jwts.claims();
            emptyClaims.put("error", "Unsupported token format");
            return emptyClaims;
        } catch (SignatureException e) {
            System.err.println("Invalid JWT signature: " + e.getMessage());
            Claims emptyClaims = Jwts.claims();
            emptyClaims.put("error", "Invalid signature");
            return emptyClaims;
        } catch (Exception e) {
            System.err.println("Error parsing JWT token: " + e.getMessage());
            // Return empty claims to avoid null pointer exceptions
            Claims emptyClaims = Jwts.claims();
            emptyClaims.put("error", "Token processing error");
            return emptyClaims;
        }
    }
    
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
} 