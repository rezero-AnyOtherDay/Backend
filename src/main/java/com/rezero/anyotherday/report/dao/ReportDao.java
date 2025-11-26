package com.rezero.anyotherday.report.dao;

import com.rezero.anyotherday.report.dto.ReportDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReportDao {

    /**
     * AI 레포트 생성
     */
    int createReport(ReportDto report);

    /**
     * 레포트 ID로 조회
     */
    ReportDto getReportById(@Param("reportId") Integer reportId);

    /**
     * 피보호자의 모든 레포트 조회 (최신 순)
     */
    List<ReportDto> getReportsByWardId(@Param("wardId") Integer wardId);

    /**
     * 피보호자의 가장 최근 레포트 1개 조회
     */
    ReportDto getLatestReportByWardId(@Param("wardId") Integer wardId);

}