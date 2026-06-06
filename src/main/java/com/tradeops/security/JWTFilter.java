package com.tradeops.security;

import com.tradeops.repo.RevokedTokenRepo;
import com.tradeops.security.service.JWTService;
import com.tradeops.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {

  private final JWTService jwtService;
  private final ApplicationContext context;
  private final CustomUserDetailsService customUserDetailsService;
  private final RevokedTokenRepo revokedTokenRepo;

  public JWTFilter(JWTService jwtService,
                   ApplicationContext context,
                   CustomUserDetailsService customUserDetailsService,
                   RevokedTokenRepo revokedTokenRepo) {
    this.jwtService = jwtService;
    this.context = context;
    this.customUserDetailsService = customUserDetailsService;
    this.revokedTokenRepo = revokedTokenRepo;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");
    String token = null;
    String username = null;

    if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
      token = authHeader.substring(7);

      try {
        if (StringUtils.hasText(token)) {

          // ── BE-015: проверка отозванного токена ───────────────────
          String tokenHash = jwtService.hashToken(token);
          if (revokedTokenRepo.existsByTokenHash(tokenHash)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token has been revoked");
            return;
          }
          // ─────────────────────────────────────────────────────────

          username = jwtService.extractUserName(token);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
          UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

          if (jwtService.validateToken(token, userDetails)) {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
          }
        }

      } catch (ExpiredJwtException ex) {
        logger.warn(String.format("The token is expired and not valid anymore for user=%s from IP=%s",
                username, request.getRemoteAddr()));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("JWT token expired");
        return;
      } catch (Exception ex) {
        logger.warn(String.format("Token validation failed for user=%s from IP=%s",
                username, request.getRemoteAddr()));
      }
    }

    filterChain.doFilter(request, response);
  }
}