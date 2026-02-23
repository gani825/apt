package com.apt.aptmanagement.configuration.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

// JWT payload(claim)에 담길 사용자 식별 정보
@Getter
@AllArgsConstructor
public class JwtUser {

    // JWT에 담을 사용자 고유 ID
    private long userId;

    // JWT에 담을 사용자 권한 (RESIDENT / ADMIN)
    private String role;
}
