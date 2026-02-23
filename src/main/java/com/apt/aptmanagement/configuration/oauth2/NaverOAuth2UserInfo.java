package com.apt.aptmanagement.configuration.oauth2;

import java.util.Map;

// 네이버 OAuth2 응답에서 사용자 정보 추출
// 네이버 응답 구조: { resultcode, message, response: { id, email, name, ... } }
// Spring Security가 "response" 키 안의 데이터를 attributes로 전달
public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    // 네이버 응답의 "response" 객체 (id, email, name 포함)
    private final Map<String, Object> attributes;

    @SuppressWarnings("unchecked")
    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        // 네이버는 사용자 정보가 "response" 키 안에 중첩되어 있음
        this.attributes = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProvider() {
        return "NAVER";
    }

    // 네이버 사용자 고유 ID는 "id" 키에 있음
    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
