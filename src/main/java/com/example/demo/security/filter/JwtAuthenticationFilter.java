package com.example.demo.security.filter;

import com.example.demo.member.domain.MemberDto;
import com.example.demo.member.service.MemberService;
import com.example.demo.security.authentication.UsernamePasswordAuthentication;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
//@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final MemberService memberService;

    @Value("${jwt.signing.key}")
    private String signingKey;

    private final String TOKEN_PREFIX = "Bearer ";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            log.info("header 문제");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        String jwt = header.substring(TOKEN_PREFIX.length());
        SecretKey key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();

        if (!validClaims(claims)) {
            log.info("claims time 문제");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        Integer userId = claims.get("userId", Integer.class);
        Optional<MemberDto> m = memberService.getMemberById(userId);

        if (!m.isPresent()) {
            log.info("member 문제");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        MemberDto member = m.get();
        GrantedAuthority authority = new SimpleGrantedAuthority(member.getType().name());
        Authentication authentication = new UsernamePasswordAuthentication(member.getUsername(), null, Arrays.asList(authority));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.equals("/login") || path.equals("/signup");
    }

    private boolean validClaims(Claims claims) {

        long currentTime = getCurrentTimeInSeconds();
        Long expireTime = claims.get("exp", Long.class);
        Long notBeforeTime = claims.get("nbf", Long.class);
        Long issuedAtTime = claims.get("iat", Long.class);

        return currentTime < expireTime
                && currentTime >= notBeforeTime
                && currentTime >= issuedAtTime;
    }

    private long getCurrentTimeInSeconds() {
        return Instant.now().getEpochSecond();
    }
}
