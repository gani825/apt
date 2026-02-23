package com.apt.aptmanagement.configuration.security;

import com.apt.aptmanagement.configuration.constants.ConstJwt;
import com.apt.aptmanagement.configuration.model.JwtUser;
import com.apt.aptmanagement.configuration.model.UserPrincipal;
import com.apt.aptmanagement.configuration.util.MyCookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

// 토큰 발급, 쿠키 저장/조회, Spring Security 인증 처리 총괄
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenManager {

    // JWT 설정 상수값 (쿠키 이름, 경로, 유효시간 등)
    private final ConstJwt constJwt;

    // 쿠키 저장/조회 유틸리티
    private final MyCookieUtil myCookieUtil;

    // AT/RT 생성 및 파싱 담당
    private final JwtTokenProvider jwtTokenProvider;

    // 로그인 성공 시 AT, RT를 쿠키에 한 번에 발급
    public void issue(HttpServletResponse res, JwtUser jwtUser) {
        setAccessTokenInCookie(res, jwtUser);
        setRefreshTokenInCookie(res, jwtUser);
    }

    // JwtUser로 AT 생성 후 쿠키에 저장
    public void setAccessTokenInCookie(HttpServletResponse res, JwtUser jwtUser) {
        String accessToken = jwtTokenProvider.generateAccessToken(jwtUser);
        setAccessTokenInCookie(res, accessToken);
    }

    // JwtUser로 RT 생성 후 쿠키에 저장
    public void setRefreshTokenInCookie(HttpServletResponse res, JwtUser jwtUser) {
        String refreshToken = jwtTokenProvider.generateRefreshToken(jwtUser);
        setRefreshTokenInCookie(res, refreshToken);
    }

    // AT 문자열을 쿠키에 저장 (HttpOnly 보안 쿠키)
    public void setAccessTokenInCookie(HttpServletResponse res, String accessToken) {
        myCookieUtil.setCookie(
                res,
                constJwt.getAccessTokenCookieName(),
                accessToken,
                constJwt.getAccessTokenCookieValiditySeconds(),
                constJwt.getAccessTokenCookiePath()
        );
    }

    // RT 문자열을 쿠키에 저장 (HttpOnly 보안 쿠키)
    public void setRefreshTokenInCookie(HttpServletResponse res, String refreshToken) {
        myCookieUtil.setCookie(
                res,
                constJwt.getRefreshTokenCookieName(),
                refreshToken,
                constJwt.getRefreshTokenCookieValiditySeconds(),
                constJwt.getRefreshTokenCookiePath()
        );
    }

    // 요청 쿠키에서 AT 추출
    public String getAccessTokenFromCookie(HttpServletRequest req) {
        return myCookieUtil.getValue(req, constJwt.getAccessTokenCookieName());
    }

    // 요청 쿠키에서 RT 추출
    public String getRefreshTokenFromCookie(HttpServletRequest req) {
        return myCookieUtil.getValue(req, constJwt.getRefreshTokenCookieName());
    }

    // 로그아웃 시 AT/RT 쿠키 삭제 (maxAge=0으로 즉시 만료)
    public void expireCookies(HttpServletResponse res) {
        myCookieUtil.setCookie(res, constJwt.getAccessTokenCookieName(), "", 0, constJwt.getAccessTokenCookiePath());
        myCookieUtil.setCookie(res, constJwt.getRefreshTokenCookieName(), "", 0, constJwt.getRefreshTokenCookiePath());
    }

    // 쿠키에서 AT를 꺼내 파싱 후 Spring Security Authentication 객체 반환
    // SecurityContextHolder에 이 객체가 담기면 인증된 사용자로 처리됨
    public Authentication getAuthentication(HttpServletRequest req) {
        String accessToken = getAccessTokenFromCookie(req);
        if (accessToken == null) {
            return null;
        }

        // AT에서 JwtUser 추출 (파싱 실패 시 null 반환)
        JwtUser jwtUser = jwtTokenProvider.getJwtUserFromToken(accessToken);
        if (jwtUser == null) {
            return null;
        }

        UserPrincipal userPrincipal = new UserPrincipal(jwtUser);
        return new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                userPrincipal.getAuthorities()
        );
    }
}
