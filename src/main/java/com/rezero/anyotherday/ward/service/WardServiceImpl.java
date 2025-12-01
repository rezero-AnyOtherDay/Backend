package com.rezero.anyotherday.ward.service;

import com.rezero.anyotherday.guardian.dao.GuardianDao;
import com.rezero.anyotherday.guardian.dto.GuardianDto;
import com.rezero.anyotherday.ward.dao.WardDao;
import com.rezero.anyotherday.ward.dto.WardDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WardServiceImpl implements WardService {

    private final WardDao wardDao;
    private final GuardianDao guardianDao;

    @Override
    public WardDto createWard(WardDto wardDto) {
        try {
            // 1. guardian 존재 여부 확인
            GuardianDto guardian = guardianDao.selectById(wardDto.getGuardianId());
            if (guardian == null) {
                throw new IllegalArgumentException("존재하지 않는 보호자입니다.");
            }

            log.info("Creating ward for guardianId: {}, name: {}, birthDate: {}, age: {}",
                wardDto.getGuardianId(), wardDto.getName(), wardDto.getBirthDate(), wardDto.getAge());

            // 2. 생성 시 diagnosis 는 항상 기본 JSON으로 고정 (클라 값 무시)
            wardDto.setDiagnosis("{\"answered\":false}");

            // 3. INSERT
            wardDao.insertWard(wardDto); // useGeneratedKeys 로 wardId 채워짐

            log.info("Ward created with wardId: {}", wardDto.getWardId());

            // 4. DB에서 다시 읽어서 반환 (created_at, status까지 포함해서 보고 싶으면)
            return wardDao.selectByWardId(wardDto.getWardId());
            // createdAt 필요 없으면 그냥 return wardDto; 해도 됨
        } catch (Exception e) {
            log.error("Error creating ward", e);
            throw e;
        }
    }

    @Override
    public List<WardDto> getWardsByGuardianId(int guardianId) {
        log.info("Fetching wards for guardianId: {}", guardianId);
        return wardDao.selectByGuardianId(guardianId);
    }

    @Override
    public WardDto getWardById(int wardId) {
        log.info("Fetching ward with wardId: {}", wardId);
        return wardDao.selectByWardId(wardId);
    }

    @Override
    public WardDto updateDiagnosis(int wardId, String diagnosisJson) {
        try {
            log.info("Updating diagnosis for wardId: {}", wardId);
            int updated = wardDao.updateDiagnosis(wardId, diagnosisJson);
            if (updated == 0) {
                throw new IllegalArgumentException("존재하지 않거나 삭제된 피보호자입니다.");
            }
            return wardDao.selectByWardId(wardId);
        } catch (Exception e) {
            log.error("Error updating diagnosis", e);
            throw e;
        }
    }
}
