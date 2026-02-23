package com.apt.aptmanagement.application.user.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// DB refresh_token 테이블과 매핑되는 Entity 클래스
@Getter
@Setter
public class AuthToken {

    // 토큰 고유 ID (PK, AUTO_INCREMENT)
    private Long tokenId;

    // 토큰 소유자 ID (FK → user)
    private Long userId;

    // 저장된 Refresh Token 문자열
    private String refreshToken;

    // 토큰 만료 시간
    private LocalDateTime expiredAt;

    // 토큰 발급 일시
    private LocalDateTime createdAt;
}
