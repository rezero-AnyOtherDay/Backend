package com.rezero.anyotherday.guardian.service;

import com.rezero.anyotherday.guardian.dao.GuardianDao;
import com.rezero.anyotherday.guardian.dto.GuardianDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuardianServiceImpl implements GuardianService {

    private final GuardianDao guardianDao;

    // 회원가입
    @Override
    @Transactional
    public GuardianDto signup(GuardianDto guardian) {
        // 1. 이메일 중복 체크
        GuardianDto existing = guardianDao.selectByEmail(guardian.getEmail());
        if (existing != null) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }

        // 2. 신규 보호자 INSERT
        guardianDao.insertGuardian(guardian);

        // 3. 다시 조회해서 반환 (생성된 guardianId 포함)
        return guardianDao.selectByEmail(guardian.getEmail());
    }

    // 로그인
    @Override
    public GuardianDto login(String email, String password) {
        GuardianDto guardian = guardianDao.selectByEmail(email);

        if (guardian == null) {
            throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
        }

        if (!guardian.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        return guardian;
    }

    // ID로 보호자 조회
    @Override
    public GuardianDto getGuardianById(Integer guardianId) {
        return guardianDao.selectById(guardianId);
    }
}
