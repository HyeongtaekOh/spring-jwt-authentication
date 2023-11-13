package com.example.demo.security.proxy;

import com.example.demo.member.domain.LoginVo;
import com.example.demo.member.domain.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AuthenticationServerProxy {

    private final RestTemplate restTemplate;

    @Value("auth.server.base.url")
    private String baseUrl;

    public void sendLogin(LoginVo loginVo) {

        String url = baseUrl + "/member/login";

        Object request = new HttpEntity<>(loginVo);
        restTemplate.postForEntity(url, request, MemberDto.class);
    }
}
