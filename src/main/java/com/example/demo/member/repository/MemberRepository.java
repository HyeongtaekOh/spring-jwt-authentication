package com.example.demo.member.repository;

import com.example.demo.member.domain.MemberDto;
import org.apache.ibatis.annotations.Mapper;

import java.sql.SQLException;
import java.util.Optional;

@Mapper
public interface MemberRepository {

    Optional<MemberDto> findById(Integer userId) throws SQLException;
    Optional<MemberDto> findByUsername(String username) throws SQLException;
    void insertMember(MemberDto memberDto) throws SQLException;
}
