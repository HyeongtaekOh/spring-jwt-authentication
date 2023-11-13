package com.example.demo.member.exception;

public class DuplicatedMemberException extends RuntimeException {

    public DuplicatedMemberException(String msg) {
        super(msg);
    }
}
