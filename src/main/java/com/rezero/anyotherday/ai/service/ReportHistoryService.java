package com.rezero.anyotherday.ai.service;

import java.util.Map;

/**
 * 이전 리포트 기록 조회 서비스
 * AI 서버의 RAG(Retrieval-Augmented Generation)에 사용할 히스토리 제공
 */
public interface ReportHistoryService {
    /**
     * 특정 피보호자의 가장 최근 리포트 요약 조회
     * 가장 최근의 진단 요약본만 반환 (AI 서버에 전송용)
     *
     * @param wardId 피보호자 ID
     * @return 리포트 요약 Map (key: 날짜, value: 요약)
     *         예: {"2025-11-27": "주의 (확률: 93.5%)"}
     *         없으면 빈 Map 반환
     */
    Map<String, String> getRecentReportSummary(Integer wardId);

    /**
     * 리포트 요약 생성
     * JSON 형식의 분석 결과를 요약 문자열로 변환
     *
     * @param analysisResult JSON 형식의 AI 분석 결과
     * @param createdAt 리포트 생성 일자
     * @return 요약 문자열
     */
    String summarizeReport(String analysisResult, String createdAt);
}
