package com.rezero.anyotherday.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 서버의 응답 DTO
 * FastAPI의 응답을 파싱하기 위한 구조
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnoseResponse {
    /**
     * AI 진단 결과 (JSON 형식)
     * 예: {
     *   "diagnosis": "경도 인지장애 의심",
     *   "risk_level": "medium",
     *   "recommendations": ["병원 방문 권고", "인지훈련"],
     *   "key_findings": ["단어 회상 어려움", "일상 업무 지연"],
     *   "confidence_score": 0.85
     * }
     */
    @JsonProperty("result")
    private Object result;

    /**
     * 진단 상태
     * 예: "success"
     */
    @JsonProperty("status")
    private String status;

    /**
     * 추가 메시지
     */
    @JsonProperty("message")
    private String message;

    /**
     * 에러 메시지 (status=error인 경우)
     */
    @JsonProperty("error")
    private String error;
}
