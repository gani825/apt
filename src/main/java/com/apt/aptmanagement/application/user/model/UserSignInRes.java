package com.apt.aptmanagement.application.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 로그인 성공 응답 데이터 (AT/RT는 쿠키로 전달, body에는 사용자 정보만 포함)
@Getter
@AllArgsConstructor
public class UserSignInRes {

    // 사용자 고유 ID
    private Long userId;

    // 사용자 이름
    private String name;

    // 사용자 권한 (RESIDENT / ADMIN)
    // 프론트에서 role 기반 라우팅 분기에 사용
    private String role;

    // 소속 세대 ID
    private Long householdId;
}
