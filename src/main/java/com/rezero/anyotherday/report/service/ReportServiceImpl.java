package com.rezero.anyotherday.report.service;

import com.rezero.anyotherday.report.dao.ReportDao;
import com.rezero.anyotherday.report.dto.ReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportDao reportDao;

    @Override
    public ReportDto createReport(ReportDto report) {
        log.info("Creating AI report for record_id: {}", report.getRecordId());
        report.setCreatedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());
        reportDao.createReport(report);
        log.info("AI report created with report_id: {}", report.getReportId());
        return report;
    }

    @Override
    public ReportDto getReportById(Integer reportId) {
        log.info("Fetching AI report with report_id: {}", reportId);
        return reportDao.getReportById(reportId);
    }

    @Override
    public ReportDto getReportByRecordId(Integer recordId) {
        log.info("Fetching AI report for record_id: {}", recordId);
        return reportDao.getReportByRecordId(recordId);
    }

    @Override
    public List<ReportDto> getReportsByWardId(Integer wardId) {
        log.info("Fetching all AI reports for ward_id: {}", wardId);
        return reportDao.getReportsByWardId(wardId);
    }

    @Override
    public void updateReport(ReportDto report) {
        log.info("Updating AI report with report_id: {}", report.getReportId());
        report.setUpdatedAt(LocalDateTime.now());
        reportDao.updateReport(report);
        log.info("AI report updated successfully");
    }

    @Override
    public void deleteReport(Integer reportId) {
        log.info("Deleting AI report with report_id: {}", reportId);
        reportDao.deleteReport(reportId);
        log.info("AI report deleted successfully");
    }

    @Override
    public List<ReportDto> getRecentReports(Integer limit) {
        log.info("Fetching {} recent AI reports", limit);
        return reportDao.getRecentReports(limit);
    }
}
