package com.example.demo.member.service;

import com.example.demo.member.domain.LoginVo;
import com.example.demo.member.domain.MemberDto;

import java.util.Optional;

public interface MemberService {

    Optional<MemberDto> getMemberById(Integer userId);
    Optional<MemberDto> getMemberByUsername(String username);
    void registMember(MemberDto memberDto);
    MemberDto loginMember(LoginVo loginVo);
}
