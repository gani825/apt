package com.apt.aptmanagement.application.household;

import com.apt.aptmanagement.application.household.model.Household;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// household 테이블 접근 MyBatis Mapper
@Mapper
public interface HouseholdMapper {

    // 동+호로 세대 조회 (회원가입 시 세대 존재 여부 확인)
    Household findByDongAndHo(@Param("dong") String dong, @Param("ho") String ho);

    // 세대 등록
    int save(Household household);

    // 전체 세대 목록 조회 (관리자 세대 관리 페이지)
    List<Household> findAll();

    // 세대 ID로 조회
    Household findById(Long householdId);

    // 세대 수정
    int update(Household household);

    // 세대 삭제
    int deleteById(Long householdId);
}
