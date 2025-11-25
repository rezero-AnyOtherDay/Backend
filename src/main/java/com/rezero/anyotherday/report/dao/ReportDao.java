package com.rezero.anyotherday.report.dao;

import com.rezero.anyotherday.report.dto.ReportDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportDao {

    /**
     * AI 레포트 생성
     */
    void createReport(ReportDto report);

    /**
     * 레포트 ID로 조회
     */
    ReportDto getReportById(Integer reportId);

    /**
     * 오디오 레코드로 레포트 조회
     */
    ReportDto getReportByRecordId(Integer recordId);

    /**
     * 피보호자의 모든 레포트 조회
     */
    List<ReportDto> getReportsByWardId(Integer wardId);

    /**
     * 레포트 업데이트
     */
    void updateReport(ReportDto report);

    /**
     * 레포트 삭제
     */
    void deleteReport(Integer reportId);

    /**
     * 최근 N개의 레포트 조회
     */
    List<ReportDto> getRecentReports(Integer limit);
}
