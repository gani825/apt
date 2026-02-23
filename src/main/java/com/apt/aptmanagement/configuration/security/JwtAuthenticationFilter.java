package com.apt.aptmanagement.configuration.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 모든 요청에 대해 1회 실행되는 JWT 인증 필터
// AT 쿠키를 검사해 인증 성공 시 SecurityContextHolder에 Authentication 저장
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 쿠키에서 AT 추출 및 Authentication 생성 담당
    private final JwtTokenManager jwtTokenManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 쿠키에서 AT를 꺼내 파싱 후 Authentication 객체 생성
        Authentication authentication = jwtTokenManager.getAuthentication(request);

        if (authentication != null) {
            // 유효한 토큰이면 SecurityContextHolder에 인증 정보 저장
            // → 이후 @AuthenticationPrincipal로 UserPrincipal 접근 가능
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("JWT 인증 성공 - userId: {}", authentication.getName());
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}
