package com.rezero.anyotherday.ward.dao;

import com.rezero.anyotherday.ward.dto.WardDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WardDao {

    // 1) 피보호자 생성
    int insertWard(WardDto ward);

    // 2) 특정 보호자의 피보호자 목록 조회
    List<WardDto> selectByGuardianId(int guardianId);
}
