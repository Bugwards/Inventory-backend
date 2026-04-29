package com.example.inventoryAuth.Utility;



import com.example.inventoryAuth.Entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final long EXPIRATION_TIME = 1000*60*30; //0.5hour
    private final String SECRETE_KEY =  "a8f9c3e7b1d4a6f92c8e5f7a1b3d6c9e8f2a4b6c7d9e1f3a5c7b9d2e4f6a8c1";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRETE_KEY.getBytes());

    public String generateToken(String username, Role role){

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .claim("role", role)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    private Claims extractClaims(String token){
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token){
        return extractClaims(token).getSubject();
    }

    private boolean isTokenExpired(String token){
        return extractClaims(token).getExpiration().before(new Date(System.currentTimeMillis()));
    }

    public boolean isTokenValid(String username, UserDetails user, String token){
        return username.equals(user.getUsername()) && !isTokenExpired(token);
    }
    
}
