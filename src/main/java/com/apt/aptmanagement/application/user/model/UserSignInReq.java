package com.apt.aptmanagement.application.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

// 로그인 요청 데이터 (POST /api/auth/login)
@Getter
@Setter
public class UserSignInReq {

    // 로그인 이메일
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    // 비밀번호 (평문, 서버에서 BCrypt 비교)
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;
}
