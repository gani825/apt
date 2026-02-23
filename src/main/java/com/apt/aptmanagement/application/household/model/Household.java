package com.apt.aptmanagement.application.household.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// DB household 테이블과 매핑되는 Entity 클래스
@Getter
@Setter
public class Household {

    // 세대 고유 ID (PK, AUTO_INCREMENT)
    private Long householdId;

    // 동 번호 (예: 101동)
    private String dong;

    // 호수 (예: 502호)
    private String ho;

    // 세대 등록 일시
    private LocalDateTime createdAt;
}
