package com.apt.aptmanagement.application.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// 회원가입 요청 데이터 (POST /api/auth/register)
@Getter
@Setter
public class UserSignUpReq {

    private Long userId;

    // 로그인에 사용할 이메일
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    // 비밀번호 (8자 이상, 영문+숫자 조합)
    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "비밀번호는 영문과 숫자를 포함해야 합니다")
    private String password;

    // 사용자 이름
    @NotBlank(message = "이름을 입력해주세요")
    private String name;

    // 휴대폰 번호
    private String phone;

    // 소속 동 번호 (예: 101동)
    @NotBlank(message = "동을 선택해주세요")
    private String dong;

    // 소속 호수 (예: 502호)
    @NotBlank(message = "호수를 입력해주세요")
    private String ho;

    // 세대 ID (서비스 계층에서 동/호로 조회 후 세팅, 클라이언트에서 전달 안 함)
    private Long householdId;
}
