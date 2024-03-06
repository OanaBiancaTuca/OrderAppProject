package com.codejava.orderapp.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JWTService {

    // Secret key for signing the JWT token
    public static final String SECRET = "8Zz5tw0Ionm3XPZZfN0NOml3z9FMfmpgXwovR9fp6ryDIoGRM8EPHAB6iHsc0abo";

    public String generateToken(String userName) {
        return Jwts.builder().setSubject(userName).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)).signWith(getSignKey(),
                        SignatureAlgorithm.HS256).compact();
    }

    public String validateToken(final String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
