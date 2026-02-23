package com.apt.aptmanagement.configuration.security;

import com.apt.aptmanagement.configuration.oauth2.CustomOAuth2UserService;
import com.apt.aptmanagement.configuration.oauth2.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// Spring Security 보안 정책 설정 클래스
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfiguration {

    // 모든 요청에서 JWT 쿠키를 검사하는 커스텀 필터
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 소셜 로그인 성공 후 사용자 정보 처리 (기존 계정 연동 or 신규 등록)
    private final CustomOAuth2UserService customOAuth2UserService;

    // 소셜 로그인 성공 시 AT/RT 쿠키 발급 및 리다이렉트 처리
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    // 보안 필터체인 설정 (인증/인가 규칙 정의)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // JWT 방식이므로 서버 세션 미사용
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // REST API 서버이므로 httpBasic, formLogin, csrf 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)

                // CORS 설정 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // URL별 인증/인가 규칙 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능한 공개 API
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()

                        // 소셜 로그인 콜백 (OAuth2)
                        .requestMatchers("/login/oauth2/**", "/oauth2/**").permitAll()

                        // Swagger UI (개발 환경)
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 관리자 전용 API (/api/admin/**)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 나머지 모든 API는 로그인 필요
                        .anyRequest().authenticated()
                )

                // 소셜 로그인 (네이버/카카오/구글) OAuth2 설정
                .oauth2Login(oauth2 -> oauth2
                        // 소셜 로그인 성공 후 사용자 정보를 처리하는 서비스 등록
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService)
                        )
                        // 소셜 로그인 성공 시 AT/RT 쿠키 발급 핸들러 등록
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                )

                // Spring Security 기본 필터 앞에 JWT 필터 삽입
                // → 매 요청마다 AT 쿠키 검사 후 인증 처리
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    // CORS 허용 설정 (Vue.js 개발 서버 허용)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 출처 (Vue 개발 서버)
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174", "http://localhost:3000"));

        // 허용할 HTTP 메서드
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 허용할 헤더
        config.setAllowedHeaders(List.of("*"));

        // 쿠키 기반 인증이므로 자격증명 허용
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
