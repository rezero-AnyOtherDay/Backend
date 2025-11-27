package com.rezero.anyotherday.ai.service;

import java.util.List;

/**
 * 이전 리포트 기록 조회 서비스
 * AI 서버의 RAG(Retrieval-Augmented Generation)에 사용할 히스토리 제공
 */
public interface ReportHistoryService {
    /**
     * 특정 피보호자의 최근 리포트 요약 조회
     * 최근 N개의 리포트를 요약한 문자열 리스트 반환
     *
     * @param wardId 피보호자 ID
     * @param limit 조회할 리포트 개수 (기본값: 5)
     * @return 리포트 요약 리스트
     *         예: ["2025-11-20: 인지기능 저하 패턴 감지", "2025-11-15: 정상"]
     */
    List<String> getReportHistory(Integer wardId, int limit);

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
