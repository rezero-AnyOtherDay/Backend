package com.rezero.anyotherday.report.service;

import com.rezero.anyotherday.report.dao.ReportDao;
import com.rezero.anyotherday.report.dto.ReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportDao reportDao;

    @Override
    public ReportDto createReport(ReportDto reportDto) {
        try {
            log.info("Creating AI report for recordId: {}", reportDto.getRecordId());
            // INSERT 후 reportDto.reportId 에 PK가 세팅됨 (useGeneratedKeys=true)
            int affected = reportDao.createReport(reportDto);
            if (affected != 1) {
                throw new IllegalStateException("AI 레포트 생성 실패");
            }
            log.info("AI report created with reportId: {}", reportDto.getReportId());
            // 방금 insert한 레포트를 다시 조회해서 반환 (createdAt/updatedAt 포함)
            return reportDao.getReportById(reportDto.getReportId());
        } catch (Exception e) {
            log.error("Error creating AI report", e);
            throw e;
        }
    }

    @Override
    public ReportDto getReportById(Integer reportId) {
        log.info("Fetching report with reportId: {}", reportId);
        return reportDao.getReportById(reportId);
    }

    @Override
    public List<ReportDto> getReportsByWardId(Integer wardId) {
        log.info("Fetching reports for wardId: {}", wardId);
        return reportDao.getReportsByWardId(wardId);
    }

    @Override
    public ReportDto getLatestReportByWardId(Integer wardId) {
        log.info("Fetching latest report for wardId: {}", wardId);
        return reportDao.getLatestReportByWardId(wardId);
    }

    @Override
    public ReportDto getReportByRecordId(Integer recordId) {
        log.info("Fetching report for recordId: {}", recordId);
        return reportDao.getReportByRecordId(recordId);
    }
}