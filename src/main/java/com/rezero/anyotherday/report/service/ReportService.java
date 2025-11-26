package com.rezero.anyotherday.report.service;

import com.rezero.anyotherday.report.dto.ReportDto;

import java.util.List;

public interface ReportService {

    /**
     * AI 레포트 생성
     * - 보통 AI 분석 완료 후 Service 레벨에서 호출
     */
    ReportDto createReport(ReportDto reportDto);

    /**
     * 레포트 ID로 단건 조회
     */
    ReportDto getReportById(Integer reportId);

    /**
     * 피보호자의 모든 레포트 조회 (최신 순)
     */
    List<ReportDto> getReportsByWardId(Integer wardId);

    /**
     * 피보호자의 가장 최근 레포트 1개 조회
     */
    ReportDto getLatestReportByWardId(Integer wardId);
}