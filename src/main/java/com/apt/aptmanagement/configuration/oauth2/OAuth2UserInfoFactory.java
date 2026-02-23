package com.apt.aptmanagement.configuration.oauth2;

import java.util.Map;

// 제공자 이름에 따라 적절한 OAuth2UserInfo 구현체를 반환하는 팩토리 클래스
public class OAuth2UserInfoFactory {

    // registrationId(제공자 이름)와 attributes로 적절한 OAuth2UserInfo 생성
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> new GoogleOAuth2UserInfo(attributes);
            case "naver"  -> new NaverOAuth2UserInfo(attributes);
            case "kakao"  -> new KakaoOAuth2UserInfo(attributes);
            default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인 제공자: " + registrationId);
        };
    }
}
