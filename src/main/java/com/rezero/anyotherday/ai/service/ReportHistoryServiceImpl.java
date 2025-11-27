package com.rezero.anyotherday.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rezero.anyotherday.report.dao.ReportDao;
import com.rezero.anyotherday.report.dto.ReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 리포트 히스토리 조회 서비스 구현
 * AI 서버의 RAG에 사용할 이전 리포트 요약 제공
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportHistoryServiceImpl implements ReportHistoryService {
    private final ReportDao reportDao;
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 특정 피보호자의 최근 리포트 요약 조회
     * 최근 N개의 리포트를 조회하고 요약한다
     * 반환 형식: {"2025-11-20": "인지기능 저하 패턴 감지", "2025-11-15": "정상"}
     */
    @Override
    public Map<String, String> getReportHistory(Integer wardId, int limit) {
        Map<String, String> history = new java.util.LinkedHashMap<>();

        try {
            log.info("Fetching report history - wardId: {}, limit: {}", wardId, limit);

            // Fetch recent reports
            List<ReportDto> reports = reportDao.getRecentReportsByWard(wardId, limit);

            if (reports == null || reports.isEmpty()) {
                log.info("No previous reports found");
                return history;
            }

            log.info("Retrieved {} reports", reports.size());

            // Convert each report to summary string
            for (ReportDto report : reports) {
                String createdDate = report.getCreatedAt().format(DATE_FORMATTER);
                String summary = summarizeReport(
                        report.getAnalysisResult(),
                        createdDate
                );
                history.put(createdDate, summary);
                log.debug("  - {}: {}", createdDate, summary);
            }

        } catch (Exception e) {
            log.warn("Failed to fetch report history: {}", e.getMessage());
            // Return empty map on failure (AI call continues)
        }

        return history;
    }

    /**
     * Generate report summary
     * Convert JSON analysis result to summary string
     * Example: "2025-11-20: Mild cognitive impairment suspected (risk: medium)"
     */
    @Override
    public String summarizeReport(String analysisResult, String createdAt) {
        try {
            if (analysisResult == null || analysisResult.isEmpty()) {
                return createdAt + ": No analysis result";
            }

            // Parse JSON
            JsonNode resultNode = objectMapper.readTree(analysisResult);

            // Extract key fields
            String diagnosis = extractFieldAsString(resultNode, "diagnosis", "No diagnosis result");
            String riskLevel = extractFieldAsString(resultNode, "risk_level", "");
            Double confidence = extractFieldAsDouble(resultNode, "confidence_score", null);

            // Generate summary string
            StringBuilder summary = new StringBuilder();
            summary.append(createdAt).append(": ").append(diagnosis);

            if (!riskLevel.isEmpty()) {
                summary.append(" (risk: ").append(formatRiskLevel(riskLevel)).append(")");
            }

            if (confidence != null) {
                summary.append(" [confidence: ").append(String.format("%.0f%%", confidence * 100)).append("]");
            }

            return summary.toString();

        } catch (Exception e) {
            log.warn("Failed to generate report summary: {}", e.getMessage());
            return createdAt + ": Error processing analysis result";
        }
    }

    /**
     * JSON 노드에서 문자열 필드 추출
     */
    private String extractFieldAsString(JsonNode node, String fieldName, String defaultValue) {
        if (node.has(fieldName)) {
            JsonNode field = node.get(fieldName);
            if (!field.isNull()) {
                return field.asText();
            }
        }
        return defaultValue;
    }

    /**
     * JSON 노드에서 Double 필드 추출
     */
    private Double extractFieldAsDouble(JsonNode node, String fieldName, Double defaultValue) {
        if (node.has(fieldName)) {
            JsonNode field = node.get(fieldName);
            if (!field.isNull() && field.isNumber()) {
                return field.asDouble();
            }
        }
        return defaultValue;
    }

    /**
     * Format risk level to readable string
     */
    private String formatRiskLevel(String riskLevel) {
        return switch (riskLevel.toLowerCase()) {
            case "low" -> "Low";
            case "medium" -> "Medium";
            case "high" -> "High";
            case "critical" -> "Critical";
            default -> riskLevel;
        };
    }
}
