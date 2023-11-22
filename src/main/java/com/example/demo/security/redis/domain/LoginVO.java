package com.example.demo.security.redis.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginVO {

    private String username;
    private String password;
}
