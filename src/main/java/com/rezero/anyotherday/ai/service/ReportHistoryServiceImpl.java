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
     * 특정 피보호자의 가장 최근 리포트 요약 조회
     * 가장 최근의 진단 요약본 하나만 반환 (AI 서버에 전송용)
     */
    @Override
    public Map<String, String> getRecentReportSummary(Integer wardId) {
        Map<String, String> history = new java.util.LinkedHashMap<>();

        try {
            log.info("Fetching recent report summary - wardId: {}", wardId);

            // Fetch only the most recent report
            List<ReportDto> reports = reportDao.getRecentReportsByWard(wardId, 1);

            if (reports == null || reports.isEmpty()) {
                log.info("No previous reports found for wardId: {}", wardId);
                return history; // Return empty map
            }

            ReportDto latestReport = reports.get(0);
            String createdDate = latestReport.getCreatedAt().format(DATE_FORMATTER);
            String summary = summarizeReport(
                    latestReport.getAnalysisResult(),
                    createdDate
            );

            // Add only the most recent report to the map
            history.put(createdDate, summary);
            log.info("Latest report summary: {} -> {}", createdDate, summary);
            return history;

        } catch (Exception e) {
            log.warn("Failed to fetch recent report summary: {}", e.getMessage());
            // Return empty map on failure (AI call continues)
            return history;
        }
    }

    /**
     * Generate report summary
     * Convert JSON analysis result to summary string
     * AI 응답 형식:
     * {
     *   "accuracy": [float, float, float],
     *   "ASR": "통화 전사",
     *   "risk": ["위험도1", "위험도2", "위험도3", "위험도4"],
     *   "explain": ["설명1", "설명2", "설명3", "설명4"],
     *   "total": "종합 소견",
     *   "summary": "과거~현재 200자 요약"
     * }
     */
    @Override
    public String summarizeReport(String analysisResult, String createdAt) {
        try {
            if (analysisResult == null || analysisResult.isEmpty()) {
                return createdAt + ": 분석 결과 없음";
            }

            // Parse JSON
            JsonNode resultNode = objectMapper.readTree(analysisResult);

            // Extract summary from new AI response format
            String summary = extractFieldAsString(resultNode, "summary", "");
            if (!summary.isEmpty()) {
                return createdAt + ": " + summary;
            }

            // Fallback: Extract risk information from new format
            JsonNode riskNode = resultNode.get("risk");
            String primaryRisk = "정보 없음";
            if (riskNode != null && riskNode.isArray() && riskNode.size() > 0) {
                primaryRisk = riskNode.get(0).asText();
            }

            // Extract accuracy for confidence level
            JsonNode accuracyNode = resultNode.get("accuracy");
            double maxAccuracy = 0;
            if (accuracyNode != null && accuracyNode.isArray() && accuracyNode.size() > 0) {
                for (JsonNode acc : accuracyNode) {
                    if (acc.isNumber()) {
                        maxAccuracy = Math.max(maxAccuracy, acc.asDouble());
                    }
                }
            }

            // Generate summary string
            StringBuilder summaryText = new StringBuilder();
            summaryText.append(createdAt).append(": ").append(primaryRisk);
            if (maxAccuracy > 0) {
                summaryText.append(" (확률: ").append(String.format("%.1f%%", maxAccuracy * 100)).append(")");
            }

            return summaryText.toString();

        } catch (Exception e) {
            log.warn("Failed to generate report summary: {}", e.getMessage());
            return createdAt + ": 분석 결과 처리 오류";
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
