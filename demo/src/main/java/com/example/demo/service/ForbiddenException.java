package com.example.demo.service;

// 본인 소유가 아닌 리소스 접근 (403)
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
