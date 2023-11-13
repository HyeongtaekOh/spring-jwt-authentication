package com.example.demo.security.filter;

import com.example.demo.member.domain.LoginVo;
import com.example.demo.member.domain.MemberDto;
import com.example.demo.member.service.MemberService;
import com.example.demo.security.authentication.UsernamePasswordAuthentication;
import com.example.demo.security.proxy.AuthenticationServerProxy;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
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

@Component
@RequiredArgsConstructor
public class CustomUsernamePasswordAuthenticationFilter extends OncePerRequestFilter {

    private final MemberService memberService;
    private final AuthenticationServerProxy serverProxy;

    @Value("jwt.signing.key")
    private String signingKey;

    private final String TOKEN_PREFIX = "bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        LoginVo loginVo = getLoginVo(request);
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

        return mapper.readValue(sb.toString(), LoginVo.class);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().equals("/login");
    }
}
