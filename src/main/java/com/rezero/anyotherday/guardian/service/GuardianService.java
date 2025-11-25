package com.rezero.anyotherday.guardian.service;

import com.rezero.anyotherday.guardian.dto.GuardianDto;

public interface GuardianService {

    // 회원가입
    GuardianDto signup(GuardianDto guardian);

    // 로그인
    GuardianDto login(String email, String password);

    // ID로 보호자 조회 (필요 시 사용)
    GuardianDto getGuardianById(Integer guardianId);
}
