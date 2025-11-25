package com.rezero.anyotherday.ward.service;

import com.rezero.anyotherday.guardian.dao.GuardianDao;
import com.rezero.anyotherday.guardian.dto.GuardianDto;
import com.rezero.anyotherday.ward.dao.WardDao;
import com.rezero.anyotherday.ward.dto.WardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class WardServiceImpl implements WardService{

    private final WardDao wardDao;
    private final GuardianDao guardianDao;

    @Override
    @Transactional
    public WardDto createWard(WardDto wardDto) {

        // 1. guardian 존재 여부 확인
        GuardianDto guardian = guardianDao.selectById(wardDto.getGuardianId());
        if (guardian == null) {
            throw new IllegalArgumentException("존재하지 않는 보호자입니다.");
        }

        // 2. diagnosis 없으면 기본 JSON 넣기
        if (wardDto.getDiagnosis() == null || wardDto.getDiagnosis().isBlank()) {
            wardDto.setDiagnosis("{\"answered\":false}");
        }

        // 3. INSERT
        wardDao.insertWard(wardDto); // useGeneratedKeys로 wardId 채워짐

        // 4. 방금 생성한 wardDto 그대로 반환
        return wardDto;
    }
}


