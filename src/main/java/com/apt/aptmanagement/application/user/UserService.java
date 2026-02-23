package com.apt.aptmanagement.application.user;

import com.apt.aptmanagement.application.household.HouseholdMapper;
import com.apt.aptmanagement.application.household.model.Household;
import com.apt.aptmanagement.application.user.model.*;
import com.apt.aptmanagement.configuration.model.JwtUser;
import com.apt.aptmanagement.configuration.security.JwtTokenManager;
import com.apt.aptmanagement.configuration.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

// 회원가입, 로그인, 로그아웃, 토큰 재발급 비즈니스 로직
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    // user, auth_token 테이블 접근
    private final UserMapper userMapper;

    // household 테이블 접근 (동/호 조회 및 등록)
    private final HouseholdMapper householdMapper;

    // BCrypt 비밀번호 암호화/비교
    private final PasswordEncoder passwordEncoder;

    // AT/RT 생성 및 쿠키 처리 총괄
    private final JwtTokenManager jwtTokenManager;

    // AT/RT 파싱 및 만료 확인
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입 처리
    // 1. 이메일 중복 확인 → 2. 세대 조회 또는 생성 → 3. 비밀번호 암호화 → 4. 사용자 등록
    @Transactional
    public void signUp(UserSignUpReq req) {

        // 이메일 중복 확인
        if (userMapper.findByEmail(req.getEmail()) != null) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다");
        }

        // 동/호로 세대 조회, 없으면 새로 등록
        Household household = householdMapper.findByDongAndHo(req.getDong(), req.getHo());
        if (household == null) {
            Household newHousehold = new Household();
            newHousehold.setDong(req.getDong());
            newHousehold.setHo(req.getHo());
            householdMapper.save(newHousehold);
            household = newHousehold;
        }

        // 비밀번호 BCrypt 암호화
        req.setPassword(passwordEncoder.encode(req.getPassword()));

        // 세대 ID 설정
        req.setHouseholdId(household.getHouseholdId());

        // DB에 회원 등록
        userMapper.signUp(req);
        log.info("회원가입 완료 - email: {}", req.getEmail());
    }

    // 로그인 처리
    // 1. 이메일로 사용자 조회 → 2. 비밀번호 검증 → 3. AT/RT 발급 → 4. RT를 DB에 저장
    @Transactional
    public UserSignInRes signIn(UserSignInReq req, HttpServletResponse res) {

        // 이메일로 사용자 조회
        User user = userMapper.findByEmail(req.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다");
        }

        // 비밀번호 비교 (평문 vs 암호화된 비밀번호)
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다");
        }

        // JWT에 담을 사용자 정보 생성
        JwtUser jwtUser = new JwtUser(user.getUserId(), user.getRole());

        // AT/RT 생성 후 HttpOnly 쿠키로 발급
        jwtTokenManager.issue(res, jwtUser);

        // RT를 DB에 저장 (재발급 시 검증용)
        saveRefreshToken(user.getUserId(), jwtTokenProvider.generateRefreshToken(jwtUser));

        log.info("로그인 성공 - userId: {}, role: {}", user.getUserId(), user.getRole());

        // 프론트에서 role 기반 라우팅에 필요한 정보 반환
        return new UserSignInRes(user.getUserId(), user.getName(), user.getRole(), user.getHouseholdId());
    }

    // 로그아웃 처리
    // 1. DB에서 RT 삭제 → 2. AT/RT 쿠키 만료
    @Transactional
    public void signOut(Long userId, HttpServletResponse res) {
        // DB에서 RT 삭제
        userMapper.deleteRefreshTokenByUserId(userId);

        // AT/RT 쿠키 즉시 만료
        jwtTokenManager.expireCookies(res);

        log.info("로그아웃 완료 - userId: {}", userId);
    }

    // AT 만료 시 RT로 AT 재발급
    // 1. 쿠키에서 RT 추출 → 2. DB에 저장된 RT와 비교 → 3. 새 AT 발급
    @Transactional
    public void refreshAccessToken(HttpServletRequest req, HttpServletResponse res) {

        // 쿠키에서 RT 추출
        String refreshToken = jwtTokenManager.getRefreshTokenFromCookie(req);
        if (refreshToken == null) {
            throw new IllegalArgumentException("Refresh Token이 없습니다");
        }

        // RT 만료 여부 확인
        if (jwtTokenProvider.isTokenExpired(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token이 만료되었습니다. 다시 로그인해주세요");
        }

        // RT에서 사용자 정보 추출
        JwtUser jwtUser = jwtTokenProvider.getJwtUserFromToken(refreshToken);
        if (jwtUser == null) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다");
        }

        // DB에 저장된 RT와 일치 여부 확인 (탈취 방지)
        AuthToken savedToken = userMapper.findRefreshTokenByUserId(jwtUser.getUserId());
        if (savedToken == null || !savedToken.getRefreshToken().equals(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다");
        }

        // 새 AT 발급 후 쿠키 갱신
        jwtTokenManager.setAccessTokenInCookie(res, jwtUser);
        log.info("AT 재발급 완료 - userId: {}", jwtUser.getUserId());
    }

    // 마이페이지 내 정보 조회
    public UserGetMeRes getMe(Long userId) {
        return userMapper.findById(userId);
    }

    // RT를 DB에 저장 (기존 RT는 삭제 후 새로 저장)
    private void saveRefreshToken(Long userId, String refreshToken) {
        userMapper.deleteRefreshTokenByUserId(userId);

        AuthToken authToken = new AuthToken();
        authToken.setUserId(userId);
        authToken.setRefreshToken(refreshToken);
        // RT 만료 시간 (7일)
        authToken.setExpiredAt(LocalDateTime.now().plusDays(7));

        userMapper.saveRefreshToken(authToken);
    }
}
