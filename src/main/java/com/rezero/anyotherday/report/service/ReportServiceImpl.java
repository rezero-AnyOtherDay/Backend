package com.rezero.anyotherday.report.service;

import com.rezero.anyotherday.report.dao.ReportDao;
import com.rezero.anyotherday.report.dto.ReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportDao reportDao;

    @Override
    public ReportDto createReport(ReportDto reportDto) {
        // INSERT 후 reportDto.reportId 에 PK가 세팅됨 (useGeneratedKeys=true)
        int affected = reportDao.createReport(reportDto);
        if (affected != 1) {
            throw new IllegalStateException("AI 레포트 생성 실패");
        }
        // 방금 insert한 레포트를 다시 조회해서 반환 (createdAt/updatedAt 포함)
        return reportDao.getReportById(reportDto.getReportId());
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDto getReportById(Integer reportId) {
        return reportDao.getReportById(reportId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDto> getReportsByWardId(Integer wardId) {
        return reportDao.getReportsByWardId(wardId);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDto getLatestReportByWardId(Integer wardId) {
        return reportDao.getLatestReportByWardId(wardId);
    }
}