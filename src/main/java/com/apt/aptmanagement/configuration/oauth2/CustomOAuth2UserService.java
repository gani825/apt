package com.apt.aptmanagement.configuration.oauth2;

import com.apt.aptmanagement.application.user.UserMapper;
import com.apt.aptmanagement.application.user.model.User;
import com.apt.aptmanagement.configuration.model.JwtUser;
import com.apt.aptmanagement.configuration.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

// 소셜 로그인 성공 후 사용자 정보를 처리하는 서비스
// 기존 소셜 계정이면 조회, 없으면 신규 등록 후 UserPrincipal 반환
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    // user 테이블 접근 (소셜 계정 조회/등록)
    private final UserMapper userMapper;

    // BCrypt 비밀번호 암호화 (소셜 로그인 시 랜덤 비밀번호 생성용)
    private final PasswordEncoder passwordEncoder;

    // 소셜 로그인 성공 시 Spring Security가 호출하는 메서드
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 부모 클래스에서 소셜 제공자 API를 호출해 사용자 정보 받아옴
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 소셜 제공자 이름 (google / naver / kakao)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 제공자별 응답 구조 차이를 추상화해서 공통 인터페이스로 추출
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId,
                oAuth2User.getAttributes()
        );

        log.info("소셜 로그인 시도 - provider: {}, providerId: {}, email: {}",
                userInfo.getProvider(), userInfo.getProviderId(), userInfo.getEmail());

        // DB에서 기존 소셜 계정 조회
        User existingUser = userMapper.findByProviderAndProviderId(
                userInfo.getProvider(),
                userInfo.getProviderId()
        );

        User user;
        if (existingUser != null) {
            // 기존 소셜 계정 → 조회된 사용자 정보 사용
            user = existingUser;
            log.info("기존 소셜 계정 로그인 - userId: {}", user.getUserId());
        } else {
            // 신규 소셜 계정 → DB에 등록
            user = registerOAuthUser(userInfo);
            log.info("신규 소셜 계정 등록 - userId: {}", user.getUserId());
        }

        // Spring Security UserPrincipal 반환 (SecurityContextHolder에 저장됨)
        JwtUser jwtUser = new JwtUser(user.getUserId(), user.getRole());
        return new UserPrincipal(jwtUser);
    }

    // 소셜 로그인 신규 사용자 DB 등록
    // password NOT NULL이므로 랜덤 비밀번호를 생성해서 저장
    // 소셜 로그인은 세대 정보가 없으므로 household_id는 null로 저장
    private User registerOAuthUser(OAuth2UserInfo userInfo) {
        User newUser = new User();
        newUser.setEmail(userInfo.getEmail());
        newUser.setName(userInfo.getName());
        // password NOT NULL → 소셜 로그인 시 랜덤 비밀번호 BCrypt 암호화 후 저장
        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        newUser.setProvider(userInfo.getProvider().toUpperCase()); // LOCAL/GOOGLE/KAKAO/NAVER
        newUser.setProviderId(userInfo.getProviderId());
        newUser.setRole("RESIDENT");       // 소셜 로그인은 기본 입주민 권한
        newUser.setStatus("PENDING");      // 2차에서 승인 로직 연동

        userMapper.signUpOAuth(newUser);
        return newUser;
    }
}
