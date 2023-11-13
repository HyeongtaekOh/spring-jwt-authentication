package com.example.demo.member.service;

import com.example.demo.member.domain.LoginVo;
import com.example.demo.member.domain.MemberDto;
import com.example.demo.member.domain.SignupVo;
import com.example.demo.member.exception.DuplicatedMemberException;
import com.example.demo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final PasswordEncoder encoder;
    private final MemberRepository memberRepository;

    @Override
    public Optional<MemberDto> getMemberById(Integer userId) {
        try {
            return memberRepository.findById(userId);
        } catch (SQLException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

    @Override
    public Optional<MemberDto> getMemberByUsername(String username) {
        try {
            return memberRepository.findByUsername(username);
        } catch (SQLException e) {
            throw new UsernameNotFoundException("존재하지 않는 사용자입니다.");
        }
    }

    @Override
    public void registMember(MemberDto memberDto) {

        try {
            Optional<MemberDto> m = getMemberByUsername(memberDto.getUsername());

            if (m.isPresent()) {
                throw new DuplicatedMemberException("이미 존재하는 사용자입니다.");
            }

            memberDto.setPassword(encoder.encode(memberDto.getPassword()));
            memberRepository.insertMember(memberDto);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MemberDto loginMember(LoginVo loginVo) {

        Optional<MemberDto> m = getMemberByUsername(loginVo.getUsername());

        if (!m.isPresent()) {
            throw new UsernameNotFoundException("존재하지 않는 사용자입니다.");
        }

        MemberDto member = m.get();

        if (encoder.matches(loginVo.getPassword(), member.getPassword())) {
            return member;
        } else {
            throw new BadCredentialsException("인증 정보가 잘못되었습니다.");
        }
    }
}
