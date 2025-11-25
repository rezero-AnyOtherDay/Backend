package com.rezero.anyotherday.ward.dao;

import com.rezero.anyotherday.ward.dto.WardDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WardDao {

    // 1) 피보호자 생성
    int insertWard(WardDto ward);

    // 2) 보호자 기준 목록 조회
    List<WardDto> selectByGuardianId(@Param("guardianId") int guardianId);

    // 3) ward_id 기준 단건 조회
    WardDto selectByWardId(@Param("wardId") int wardId);

    // 4) 자가진단(diagnosis)만 수정
    int updateDiagnosis(
            @Param("wardId") int wardId,
            @Param("diagnosis") String diagnosisJson);
}

