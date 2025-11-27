package com.rezero.anyotherday.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI 서버의 응답 DTO
 * AI 서버 응답 형식:
 * {
 *   "accuracy": [뇌졸중%, 퇴행성%, 정상%],
 *   "ASR": "전체 대화 내용",
 *   "risk": ["뇌졸중", "치매", "파킨슨", "루게릭"],
 *   "explain": ["뇌졸중 설명", "치매 설명", "파킨슨 설명", "루게릭 설명"],
 *   "summary": "종합 소견 (200~300자)"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnoseResponse {
    /**
     * 질병별 정확도 (%)
     * [뇌졸중%, 퇴행성%, 정상%]
     */
    @JsonProperty("accuracy")
    private List<Double> accuracy;

    /**
     * 자동음성인식(ASR) 결과 - 전체 대화 내용
     */
    @JsonProperty("ASR")
    private String asr;

    /**
     * 위험 질병 목록
     * ["뇌졸중", "치매", "파킨슨", "루게릭"]
     */
    @JsonProperty("risk")
    private List<String> risk;

    /**
     * 질병별 설명
     * ["뇌졸중 설명", "치매 설명", "파킨슨 설명", "루게릭 설명"]
     */
    @JsonProperty("explain")
    private List<String> explain;

    /**
     * 종합 소견 (200~300자)
     */
    @JsonProperty("summary")
    private String summary;

    /**
     * 진단 상태 (status는 응답에 없음 - 응답이 있으면 성공)
     */
    @JsonProperty("status")
    private String status;

    /**
     * 에러 메시지
     */
    @JsonProperty("error")
    private String error;
}
