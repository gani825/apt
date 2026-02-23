package com.apt.aptmanagement.configuration.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

// application.yml의 constants.jwt 하위 값들을 자동으로 읽어와 바인딩
@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "constants.jwt")
@ToString
public class ConstJwt {

    // JWT 발급자 식별값 (예: apt-management)
    private final String issuer;

    // 토큰 타입 헤더값 (예: JWT)
    private final String bearerFormat;

    // JWT payload에서 사용자 정보를 꺼낼 때 사용하는 키 이름
    private final String claimKey;

    // 토큰 서명(Signature)에 사용되는 비밀 키 (Base64 인코딩)
    private final String secretKey;

    // Access Token 쿠키 이름 (예: at)
    private final String accessTokenCookieName;

    // Access Token 쿠키 유효 경로 (예: /)
    private final String accessTokenCookiePath;

    // Access Token 쿠키 유효 시간 (초 단위)
    private final int accessTokenCookieValiditySeconds;

    // Access Token 유효 시간 (밀리초 단위)
    private final long accessTokenValidityMilliseconds;

    // Refresh Token 쿠키 이름 (예: rt)
    private final String refreshTokenCookieName;

    // Refresh Token 쿠키 유효 경로 (예: /api/auth/refresh)
    private final String refreshTokenCookiePath;

    // Refresh Token 쿠키 유효 시간 (초 단위)
    private final int refreshTokenCookieValiditySeconds;

    // Refresh Token 유효 시간 (밀리초 단위)
    private final long refreshTokenValidityMilliseconds;
}
