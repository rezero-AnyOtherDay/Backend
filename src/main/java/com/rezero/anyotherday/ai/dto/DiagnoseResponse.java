package com.rezero.anyotherday.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI 서버의 응답 DTO
 * AI 서버 응답 형식:
 * {
 *   "accuracy": [뇌졸중%, 퇴행성뇌질환%, 정상%],
 *   "ASR": "통화 전사 데이터",
 *   "risk": ["뇌졸중 위험도", "치매 위험도", "파킨슨병 위험도", "루게릭병 위험도"],
 *   "explain": ["뇌졸중 설명", "치매 설명", "파킨슨병 설명", "루게릭병 설명"],
 *   "total": "종합 소견 3문장(75자 내외)",
 *   "summary": "과거~현재 200자 요약"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiagnoseResponse {
    /**
     * 질병별 정확도 (확률)
     * [뇌졸중확률, 퇴행성뇌질환확률, 정상확률]
     */
    @JsonProperty("accuracy")
    private List<Double> accuracy;

    /**
     * 자동음성인식(ASR) 결과 - 전체 대화 전사 내용
     */
    @JsonProperty("ASR")
    private String asr;

    /**
     * 위험 수준 (4가지 질병별)
     * ["뇌졸중 위험도", "치매 위험도", "파킨슨병 위험도", "루게릭병 위험도"]
     */
    @JsonProperty("risk")
    private List<String> risk;

    /**
     * 질병별 상세 설명 (4가지 질병별)
     * ["뇌졸중 설명", "치매 설명", "파킨슨병 설명", "루게릭병 설명"]
     */
    @JsonProperty("explain")
    private List<String> explain;

    /**
     * 종합 소견 - 리포트 맨 위에 표시 (3문장, 75자 내외)
     */
    @JsonProperty("total")
    private String total;

    /**
     * 과거~현재 요약 - DB의 이전 대화 요약 저장 (200자 내외)
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
