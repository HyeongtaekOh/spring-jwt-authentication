package com.example.demo.security.redis.controller;

import com.example.demo.security.redis.domain.LoginVO;
import com.example.demo.security.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RedisController {

    private final RedisService redisService;

    @PostMapping("/redis")
    public ResponseEntity<String> redis(@RequestBody LoginVO loginVO) {

        if ("test".equals(loginVO.getUsername()) && "1234".equals(loginVO.getPassword())) {
            redisService.setData("test", "first redis token", 60000L);
            return ResponseEntity.ok("success");
        }

        return ResponseEntity.status(401).build();
    }

    @GetMapping("/redis")
    public ResponseEntity<String> redis(String username) {

        String token = redisService.getData(username);

        if (token == null) {
            throw new BadCredentialsException("token is null");
        }
        return ResponseEntity.ok(token);
    }
}
