package com.apt.aptmanagement.application.user.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// DB user 테이블과 매핑되는 Entity 클래스
@Getter
@Setter
public class User {

    // 사용자 고유 ID (PK, AUTO_INCREMENT)
    private Long userId;

    // 소속 세대 ID (FK → household)
    private Long householdId;

    // 로그인 이메일 (UNIQUE)
    private String email;

    // BCrypt 암호화된 비밀번호
    private String password;

    // 사용자 이름
    private String name;

    // 휴대폰 번호
    private String phone;

    // 사용자 권한 (RESIDENT / ADMIN)
    private String role;

    // 가입 승인 상태 (PENDING / APPROVED / REJECTED) ★2차에서 체크 로직 추가
    private String status;

    // 승인한 관리자 ID (FK → user, 2차 구현)
    private Long approvedBy;

    // 승인/거부 일시 (2차 구현)
    private LocalDateTime approvedAt;

    // 소셜 로그인 제공자 (naver / kakao / google, 일반 로그인은 null)
    private String provider;

    // 소셜 로그인 제공자의 고유 사용자 ID
    private String providerId;

    // 계정 생성일시
    private LocalDateTime createdAt;

    // 계정 수정일시
    private LocalDateTime updatedAt;
}
