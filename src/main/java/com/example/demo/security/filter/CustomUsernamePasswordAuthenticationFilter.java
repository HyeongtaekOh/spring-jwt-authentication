package com.example.demo.security.filter;

import com.example.demo.member.domain.LoginVo;
import com.example.demo.member.domain.MemberDto;
import com.example.demo.member.service.MemberService;
import com.example.demo.security.authentication.UsernamePasswordAuthentication;
import com.example.demo.security.proxy.AuthenticationServerProxy;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
//@Component
@RequiredArgsConstructor
public class CustomUsernamePasswordAuthenticationFilter extends OncePerRequestFilter {

    private final MemberService memberService;
    private final AuthenticationServerProxy serverProxy;

    @Value("${jwt.signing.key}")
    private String signingKey;

    private final String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("Custom Filtering...");
        LoginVo loginVo = getLoginVo(request);

        if (loginVo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request");
            return;
        }

        MemberDto member = memberService.loginMember(loginVo);

        if (member != null) {
            SecretKey key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
            Map<String, Integer> map = new HashMap<>();
            map.put("userId", member.getUserId());
            String jwtToken = Jwts.builder()
                    .setClaims(map)
                    .signWith(key)
                    .compact();
            response.setHeader("Authorization", TOKEN_PREFIX + jwtToken);
        }

//        GrantedAuthority authority = new SimpleGrantedAuthority(member.getType().name());
//        Authentication authentication = new UsernamePasswordAuthentication(member.getUsername(), null, Arrays.asList(authority));
//        SecurityContextHolder.getContext().setAuthentication(authentication);

//        filterChain.doFilter(request, response);
    }

    public LoginVo getLoginVo(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line = null;
        BufferedReader br = request.getReader();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(sb.toString(), LoginVo.class);
        } catch (MismatchedInputException e) {
            return null;
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        log.info("Filter get servlet path : {}", path);
        return !path.equals("/login") && !path.equals("/login/");
    }
}
