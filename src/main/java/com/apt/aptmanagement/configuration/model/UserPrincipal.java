package com.apt.aptmanagement.configuration.model;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Spring Security가 인증 처리 시 SecurityContextHolder에 담는 사용자 정보 객체
// UserDetails (일반 로그인) + OAuth2User (소셜 로그인) 둘 다 구현
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails, OAuth2User {

    // JWT에서 파싱한 사용자 정보 (userId, role 포함)
    private final JwtUser jwtUser;

    // 현재 인증된 사용자의 userId 반환
    public long getUserId() {
        return jwtUser.getUserId();
    }

    // 현재 인증된 사용자의 role 반환 (RESIDENT / ADMIN)
    public String getRole() {
        return jwtUser.getRole();
    }

    // 사용자 권한 목록 반환 (ROLE_RESIDENT / ROLE_ADMIN 형태로 변환)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + jwtUser.getRole()));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return String.valueOf(jwtUser.getUserId());
    }

    // OAuth2User 인터페이스 구현
    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("userId", jwtUser.getUserId());
        attributes.put("role", jwtUser.getRole());
        return attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(jwtUser.getUserId());
    }
}
