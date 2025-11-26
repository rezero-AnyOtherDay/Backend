package com.rezero.anyotherday.report.controller;

import com.rezero.anyotherday.report.dto.ReportDto;
import com.rezero.anyotherday.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 레포트 단건 조회
     * GET /api/v1/reports/{reportId}
     */
    @GetMapping("/{reportId}")
    public ReportDto getReportById(@PathVariable Integer reportId) {
        return reportService.getReportById(reportId);
    }

    /**
     * 피보호자의 모든 레포트 조회 (최신 순)
     * GET /api/v1/reports/ward/{wardId}
     */
    @GetMapping("/ward/{wardId}")
    public List<ReportDto> getReportsByWard(@PathVariable Integer wardId) {
        return reportService.getReportsByWardId(wardId);
    }

    /**
     * 피보호자의 가장 최근 레포트 1개 조회
     * GET /api/v1/reports/ward/{wardId}/latest
     */
    @GetMapping("/ward/{wardId}/latest")
    public ReportDto getLatestReportByWard(@PathVariable Integer wardId) {
        return reportService.getLatestReportByWardId(wardId);
    }
}