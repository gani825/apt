package com.apt.aptmanagement.configuration.oauth2;

// 각 소셜 로그인 제공자(네이버/카카오/구글)의 사용자 정보 추출 인터페이스
// 제공자마다 응답 JSON 구조가 달라 각각 구현체를 따로 만든다
public interface OAuth2UserInfo {

    // 소셜 로그인 제공자 이름 반환 (naver / kakao / google)
    String getProvider();

    // 제공자가 발급한 사용자 고유 ID 반환
    String getProviderId();

    // 사용자 이메일 반환
    String getEmail();

    // 사용자 이름 반환
    String getName();
}
