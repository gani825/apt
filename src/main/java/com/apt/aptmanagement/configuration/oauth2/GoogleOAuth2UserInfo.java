package com.apt.aptmanagement.configuration.oauth2;

import java.util.Map;

// 구글 OAuth2 응답에서 사용자 정보 추출
// 구글 응답 구조: { sub, email, name, picture, ... }
public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

    // 구글이 반환한 attributes 맵 (sub, email, name 등)
    private final Map<String, Object> attributes;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "GOOGLE";
    }

    // 구글 사용자 고유 ID는 "sub" 키에 있음
    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
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
