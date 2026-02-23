package com.apt.aptmanagement.application.user.model;

import lombok.Getter;
import lombok.Setter;

// 마이페이지 내 정보 조회 응답 데이터 (GET /api/users/me)
@Getter
@Setter
public class UserGetMeRes {

    // 사용자 고유 ID
    private Long userId;

    // 사용자 이메일
    private String email;

    // 사용자 이름
    private String name;

    // 휴대폰 번호
    private String phone;

    // 사용자 권한 (RESIDENT / ADMIN)
    private String role;

    // 가입 승인 상태 (PENDING / APPROVED / REJECTED)
    private String status;

    // 소속 세대 ID
    private Long householdId;

    // 소속 동 번호
    private String dong;

    // 소속 호수
    private String ho;

    // 소셜 로그인 제공자 (naver / kakao / google, 일반 로그인은 null)
    private String provider;
}
