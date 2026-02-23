package com.apt.aptmanagement.application.user;

import com.apt.aptmanagement.application.user.model.AuthToken;
import com.apt.aptmanagement.application.user.model.User;
import com.apt.aptmanagement.application.user.model.UserGetMeRes;
import com.apt.aptmanagement.application.user.model.UserSignUpReq;
import org.apache.ibatis.annotations.Mapper;

// user, refresh_token 테이블 접근 MyBatis Mapper
@Mapper
public interface UserMapper {

    // 회원 등록 (INSERT INTO user)
    int signUp(UserSignUpReq req);

    // 이메일로 사용자 조회 (로그인, 중복 체크용)
    User findByEmail(String email);

    // userId로 사용자 조회 (마이페이지, 토큰 재발급용)
    UserGetMeRes findById(Long userId);

    // 소셜 로그인 제공자 + 제공자 ID로 사용자 조회 (소셜 로그인 기존 계정 확인)
    User findByProviderAndProviderId(String provider, String providerId);

    // 소셜 로그인 신규 사용자 등록
    int signUpOAuth(User user);

    // RT 저장 (로그인 시 DB에 RT 보관)
    int saveRefreshToken(AuthToken authToken);

    // userId로 RT 조회 (토큰 재발급 시 DB RT 검증)
    AuthToken findRefreshTokenByUserId(Long userId);

    // userId로 RT 삭제 (로그아웃 시 DB RT 제거)
    int deleteRefreshTokenByUserId(Long userId);

    // RT 문자열로 RT 조회 (만료/탈취 여부 확인용)
    AuthToken findByRefreshToken(String refreshToken);
}
