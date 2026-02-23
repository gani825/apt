package com.apt.aptmanagement.application.user;

import com.apt.aptmanagement.application.user.model.*;
import com.apt.aptmanagement.configuration.model.ResultResponse;
import com.apt.aptmanagement.configuration.model.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// 인증 관련 API 컨트롤러 (회원가입, 로그인, 로그아웃, 토큰 재발급)
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    // 인증 비즈니스 로직
    private final UserService userService;

    // 회원가입 (POST /api/auth/register)
    // 인증 불필요 - SecurityConfig에서 permitAll 설정
    @PostMapping("/register")
    public ResultResponse<Void> register(@Valid @RequestBody UserSignUpReq req) {
        userService.signUp(req);
        return new ResultResponse<>("회원가입 성공", null);
    }

    // 로그인 (POST /api/auth/login)
    // 성공 시 AT/RT를 HttpOnly 쿠키로 발급, body에는 사용자 정보만 반환
    @PostMapping("/login")
    public ResultResponse<UserSignInRes> login(@Valid @RequestBody UserSignInReq req,
                                               HttpServletResponse res) {
        UserSignInRes result = userService.signIn(req, res);
        return new ResultResponse<>("로그인 성공", result);
    }

    // 로그아웃 (POST /api/auth/logout)
    // DB에서 RT 삭제 + AT/RT 쿠키 만료 처리
    @PostMapping("/logout")
    public ResultResponse<Void> logout(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                       HttpServletResponse res) {
        userService.signOut(userPrincipal.getUserId(), res);
        return new ResultResponse<>("로그아웃 성공", null);
    }

    // AT 재발급 (POST /api/auth/refresh)
    // 쿠키의 RT를 검증해 새 AT 발급
    @PostMapping("/refresh")
    public ResultResponse<Void> refresh(HttpServletRequest req, HttpServletResponse res) {
        userService.refreshAccessToken(req, res);
        return new ResultResponse<>("토큰 재발급 성공", null);
    }

    // 내 정보 조회 (GET /api/auth/me)
    // 마이페이지에서 사용자 정보 확인용
    @GetMapping("/me")
    public ResultResponse<UserGetMeRes> getMe(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserGetMeRes result = userService.getMe(userPrincipal.getUserId());
        return new ResultResponse<>("내 정보 조회 성공", result);
    }
}
