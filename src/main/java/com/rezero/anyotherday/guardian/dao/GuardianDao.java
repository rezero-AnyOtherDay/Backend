package com.rezero.anyotherday.guardian.dao;

import com.rezero.anyotherday.guardian.dto.GuardianDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GuardianDao {

    // 1) 보호자 회원가입
    int insertGuardian(GuardianDto guardian);

    // 2) 이메일로 보호자 1명 조회 (중복 체크, 로그인용)
    GuardianDto selectByEmail(@Param("email") String email);

    // 3) ID로 보호자 1명 조회
    GuardianDto selectById(@Param("guardianId") Integer guardianId);
}
