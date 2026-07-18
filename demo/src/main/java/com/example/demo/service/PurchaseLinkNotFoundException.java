package com.example.demo.service;

// search_keyword에 매핑된 상품 링크가 없을 때(시드 누락 등). 정상 운영에서는 발생하지 않는 안전망이다.
public class PurchaseLinkNotFoundException extends RuntimeException {

    public PurchaseLinkNotFoundException(String message) {
        super(message);
    }
}
