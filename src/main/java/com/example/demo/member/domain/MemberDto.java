package com.example.demo.member.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberDto {

    private Integer userId;
    private String username;
    private String password;
    private String email;
    private MemberType type;
}
