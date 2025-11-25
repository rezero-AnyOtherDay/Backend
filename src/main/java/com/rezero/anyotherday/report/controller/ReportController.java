package com.rezero.anyotherday.report.controller;

import com.rezero.anyotherday.report.dto.ReportDto;
import com.rezero.anyotherday.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * AI 레포트 생성
     * POST /api/v1/reports
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createReport(@RequestBody ReportDto reportDto) {
        try {
            log.info("Creating AI report for record_id: {}", reportDto.getRecordId());
            ReportDto report = reportService.createReport(reportDto);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", report);
            response.put("message", "AI report created successfully");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating AI report", e);
            return buildErrorResponse("Failed to create AI report", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 레포트 ID로 조회
     * GET /api/v1/reports/{reportId}
     */
    @GetMapping("/{reportId}")
    public ResponseEntity<Map<String, Object>> getReportById(@PathVariable Integer reportId) {
        try {
            log.info("Fetching AI report with report_id: {}", reportId);
            ReportDto report = reportService.getReportById(reportId);

            if (report == null) {
                return buildErrorResponse("Report not found", HttpStatus.NOT_FOUND);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", report);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching AI report", e);
            return buildErrorResponse("Failed to fetch AI report", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 오디오 레코드로 레포트 조회
     * GET /api/v1/reports/record/{recordId}
     */
    @GetMapping("/record/{recordId}")
    public ResponseEntity<Map<String, Object>> getReportByRecordId(@PathVariable Integer recordId) {
        try {
            log.info("Fetching AI report for record_id: {}", recordId);
            ReportDto report = reportService.getReportByRecordId(recordId);

            if (report == null) {
                return buildErrorResponse("Report not found for this record", HttpStatus.NOT_FOUND);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", report);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching AI report", e);
            return buildErrorResponse("Failed to fetch AI report", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 피보호자의 모든 레포트 조회
     * GET /api/v1/reports/ward/{wardId}
     */
    @GetMapping("/ward/{wardId}")
    public ResponseEntity<Map<String, Object>> getReportsByWardId(@PathVariable Integer wardId) {
        try {
            log.info("Fetching all AI reports for ward_id: {}", wardId);
            List<ReportDto> reports = reportService.getReportsByWardId(wardId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", reports);
            response.put("count", reports.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching AI reports", e);
            return buildErrorResponse("Failed to fetch AI reports", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 레포트 업데이트
     * PUT /api/v1/reports/{reportId}
     */
    @PutMapping("/{reportId}")
    public ResponseEntity<Map<String, Object>> updateReport(
            @PathVariable Integer reportId,
            @RequestBody ReportDto updateDto) {
        try {
            log.info("Updating AI report with report_id: {}", reportId);
            updateDto.setReportId(reportId);
            reportService.updateReport(updateDto);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "AI report updated successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating AI report", e);
            return buildErrorResponse("Failed to update AI report", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 레포트 삭제
     * DELETE /api/v1/reports/{reportId}
     */
    @DeleteMapping("/{reportId}")
    public ResponseEntity<Map<String, Object>> deleteReport(@PathVariable Integer reportId) {
        try {
            log.info("Deleting AI report with report_id: {}", reportId);
            reportService.deleteReport(reportId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "AI report deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting AI report", e);
            return buildErrorResponse("Failed to delete AI report", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 최근 레포트 조회
     * GET /api/v1/reports?limit=10
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getRecentReports(
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            log.info("Fetching {} recent AI reports", limit);
            List<ReportDto> reports = reportService.getRecentReports(limit);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", reports);
            response.put("count", reports.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching recent AI reports", e);
            return buildErrorResponse("Failed to fetch AI reports", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 에러 응답 생성
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }
}
