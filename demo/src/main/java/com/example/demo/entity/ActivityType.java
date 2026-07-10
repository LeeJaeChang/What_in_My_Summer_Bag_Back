package com.example.demo.entity;

/**
 * 이름을 소문자로 둔 이유: JSON 바디("sea","camping"...)와 DB CHECK 제약값이 그대로 일치해서
 * Jackson/JPA 양쪽에서 별도 매핑 코드 없이 문자열이 그대로 통한다.
 */
public enum ActivityType {
    sea, camping, city, cafe
}
