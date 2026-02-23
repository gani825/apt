package com.apt.aptmanagement.configuration.oauth2;

import java.util.Map;

// 카카오 OAuth2 응답에서 사용자 정보 추출
// 카카오 응답 구조: { id, kakao_account: { email, profile: { nickname } } }
public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    // 카카오 응답 전체 attributes (id, kakao_account 등)
    private final Map<String, Object> attributes;

    // 카카오 계정 정보 (email, profile 포함)
    private final Map<String, Object> kakaoAccount;

    // 프로필 정보 (nickname 포함)
    private final Map<String, Object> kakaoProfile;

    @SuppressWarnings("unchecked")
    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
    }

    @Override
    public String getProvider() {
        return "KAKAO";
    }

    // 카카오 사용자 고유 ID는 최상위 "id" 키에 있음 (Long 타입이므로 변환 필요)
    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    // 카카오 이메일은 kakao_account.email에 있음
    @Override
    public String getEmail() {
        return (String) kakaoAccount.get("email");
    }

    // 카카오 닉네임은 kakao_account.profile.nickname에 있음
    @Override
    public String getName() {
        return (String) kakaoProfile.get("nickname");
    }
}
