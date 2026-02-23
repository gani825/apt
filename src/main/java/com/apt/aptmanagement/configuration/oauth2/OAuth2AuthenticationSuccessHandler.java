package com.apt.aptmanagement.configuration.oauth2;

import com.apt.aptmanagement.application.user.UserMapper;
import com.apt.aptmanagement.application.user.model.AuthToken;
import com.apt.aptmanagement.configuration.model.JwtUser;
import com.apt.aptmanagement.configuration.model.UserPrincipal;
import com.apt.aptmanagement.configuration.security.JwtTokenManager;
import com.apt.aptmanagement.configuration.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

// 소셜 로그인 성공 시 JWT AT/RT 발급 후 Vue 앱으로 리다이렉트
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    // AT/RT 발급 및 쿠키 저장 총괄
    private final JwtTokenManager jwtTokenManager;

    // RT 생성 (DB 저장용)
    private final JwtTokenProvider jwtTokenProvider;

    // RT DB 저장
    private final UserMapper userMapper;

    // 소셜 로그인 성공 후 리다이렉트할 Vue 앱 주소
    private static final String REDIRECT_URL = "http://localhost:5173/oauth2/callback";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // 인증 완료된 사용자 정보 추출
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        JwtUser jwtUser = new JwtUser(userPrincipal.getUserId(), userPrincipal.getRole());

        // AT/RT를 HttpOnly 쿠키로 발급
        jwtTokenManager.issue(response, jwtUser);

        // RT를 DB에 저장 (AT 재발급 시 검증용)
        String refreshToken = jwtTokenProvider.generateRefreshToken(jwtUser);
        userMapper.deleteRefreshTokenByUserId(jwtUser.getUserId());

        AuthToken authToken = new AuthToken();
        authToken.setUserId(jwtUser.getUserId());
        authToken.setRefreshToken(refreshToken);
        authToken.setExpiredAt(LocalDateTime.now().plusDays(7));
        userMapper.saveRefreshToken(authToken);

        log.info("소셜 로그인 성공 - userId: {}, role: {}", jwtUser.getUserId(), jwtUser.getRole());

        // Vue 앱 소셜 로그인 콜백 페이지로 리다이렉트
        // 프론트에서 role 기반 대시보드로 다시 이동함
        response.sendRedirect(REDIRECT_URL + "?role=" + jwtUser.getRole());
    }
}
