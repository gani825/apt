package com.apt.aptmanagement.configuration.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 모든 API 응답에 사용하는 공통 응답 포맷
@Getter
@AllArgsConstructor
public class ResultResponse<T> {

    // 처리 결과 메시지 (예: "로그인 성공", "회원가입 실패")
    private String resultMessage;

    // 실제 응답 데이터 (제네릭 타입으로 어떤 데이터든 담을 수 있음)
    private T resultData;
}
