package com.example.demo.member.controller;

import com.example.demo.member.domain.LoginVo;
import com.example.demo.member.domain.MemberDto;
import com.example.demo.member.domain.MemberType;
import com.example.demo.member.domain.SignupVo;
import com.example.demo.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<?> login(LoginVo loginVo) {

        MemberDto member = memberService.loginMember(loginVo);

        if (member != null) {
            return ResponseEntity.ok(member);
        } else {
            throw new BadCredentialsException("인증 정보가 잘못되었습니다.");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(SignupVo signupVo) {

        MemberDto memberDto = new MemberDto();
        memberDto.setUsername(signupVo.getUsername());
        memberDto.setPassword(signupVo.getPassword());
        memberDto.setEmail(signupVo.getEmail());
        memberDto.setType(MemberType.USER);
        memberService.registMember(memberDto);
        Map<String, Integer> response = new HashMap<>();
        response.put("userId", memberDto.getUserId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
