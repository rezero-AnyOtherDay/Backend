package com.rezero.anyotherday.ward.service;

import com.rezero.anyotherday.ward.dto.WardDto;
import java.util.List;

public interface WardService {

    // 1) 피보호자 생성
    WardDto createWard(WardDto wardDto);

    // 2) 보호자 기준 피보호자 목록 조회
    List<WardDto> getWardsByGuardianId(int guardianId);

    // 3) 피보호자 단건 조회
    WardDto getWardById(int wardId);

    // 4) 자가진단 수정
    WardDto updateDiagnosis(int wardId, String diagnosisJson);
}
