package com.example.mobile.app.ws.shared.dto;

import com.example.mobile.app.ws.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

@Component
public class Utils {
    private final Random RANDOM = new SecureRandom();
    private final String ALPHABET = "0123456789ABCDEFGHIJKLMHOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static boolean hasTokenExpired(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SecurityConstants.getTokenSecret())
                .parseClaimsJws(token).getBody();

        Date tokenExpirationDate = claims.getExpiration();
        Date todayDate = new Date();

        return tokenExpirationDate.before(todayDate);
    }

    public static String generateEmailVerificationToken(String userId) {
        String token = Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() +SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();
        return token;
    }

    public String generateUserID(int length) {
        return generateRandomString(length);
    }

    public String generateAddressId(int length) {
        return generateRandomString(length);
    }
    private String generateRandomString(int length) {
        StringBuilder returnValue = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return new String(returnValue);
    }

}
