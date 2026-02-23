package com.apt.aptmanagement.configuration.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// 쿠키 저장, 조회, 삭제 유틸리티
@Slf4j
@Component
public class MyCookieUtil {

    // 쿠키 생성 후 응답에 추가
    // HttpOnly 설정으로 JS 접근 차단 (보안 쿠키)
    public void setCookie(HttpServletResponse res, String key, String value, int maxAge, String path) {
        Cookie cookie = new Cookie(key, value);

        // 쿠키 유효 시간 (초 단위, 0이면 즉시 만료)
        cookie.setMaxAge(maxAge);

        // JS에서 쿠키 접근 불가 (XSS 공격 방어)
        cookie.setHttpOnly(true);

        // 지정 경로에서만 쿠키 전송 (RT는 /api/auth/refresh 경로에서만 전송)
        if (path != null) {
            cookie.setPath(path);
        }

        // HTTPS 환경에서는 Secure 속성 추가 필요 (운영 환경 전환 시 활성화)
        // cookie.setSecure(true);

        res.addCookie(cookie);
    }

    // 쿠키에서 특정 키의 값 반환
    public String getValue(HttpServletRequest req, String key) {
        Cookie cookie = getCookie(req, key);
        return cookie == null ? null : cookie.getValue();
    }

    // 쿠키 배열에서 특정 키의 쿠키 객체 반환
    public Cookie getCookie(HttpServletRequest req, String key) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie c : cookies) {
                if (c.getName().equals(key)) {
                    return c;
                }
            }
        }
        return null;
    }
}
