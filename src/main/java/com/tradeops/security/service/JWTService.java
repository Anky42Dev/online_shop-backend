package com.tradeops.security.service;

import com.tradeops.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HexFormat;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JWTService {

  @Value("${onlineshop.app.secret}")
  private String secretKey;

  private SecretKey getKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(Authentication authentication) {
    String username = authentication.getName();
    List<String> roles = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    Date now = new Date();
    Date expiry = new Date(now.getTime() + SecurityConstants.JWT_EXPIRATION_TIME);

    return Jwts.builder()
            .subject(username)
            .claim("roles", roles)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(getKey())
            .compact();
  }

  public String generateRefreshToken(Authentication authentication) {
    String username = authentication.getName();
    Date currentDate = new Date();
    Date expirationDate = new Date(currentDate.getTime() + SecurityConstants.JWT_REFRESH_EXPIRATION_TIME);

    return Jwts.builder()
            .subject(username)
            .issuedAt(currentDate)
            .expiration(expirationDate)
            .signWith(getKey())
            .compact();
  }

  public String extractUserName(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public LocalDateTime extractExpirationAsLocalDateTime(String token) {
    Date expiration = extractClaim(token, Claims::getExpiration);
    return expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  /**
   * SHA-256 хэш токена — хранится в БД вместо raw-значения,
   * чтобы украденный дамп таблицы не давал доступа к токенам.
   */
  public String hashToken(String token) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hashBytes);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 algorithm not available", e);
    }
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    final String userName = extractUserName(token);
    return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  public boolean isTokenExpired(String token) {
    return extractClaim(token, Claims::getExpiration).before(new Date());
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
    final Claims claims = extractAllClaims(token);
    return claimResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
            .verifyWith(getKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }
}