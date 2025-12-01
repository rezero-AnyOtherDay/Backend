package com.rezero.anyotherday.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * AI 서버에 전송할 요청 DTO
 * FastAPI의 DiagnoseRequest와 동일한 구조
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnoseRequest {
    /**
     * S3에 업로드된 음성 파일의 URL
     * 예: https://s3.amazonaws.com/bucket/audio/1/uuid.mp3
     */
    @JsonProperty("audio_path")
    private String audioPath;

    /**
     * 사용자 자가진단 결과
     * 예: {
     *   "answered": true,
     *   "completedAt": "2025-12-01T10:30:00Z",
     *   "questions": [
     *     {"text": "손이나 팔에 힘이...", "answer": 3},
     *     ...
     *   ]
     * }
     */
    @JsonProperty("self_report")
    private Map<String, Object> selfReport;

    /**
     * 이전 최근 리포트 요약 (RAG용)
     * AI 서버가 dict 타입으로 기대하므로 Map 형식으로 전달
     * 가장 최근의 진단 요약본만 담음 (key: 날짜, value: 요약)
     * 예: {"2025-11-27": "주의 (확률: 93.5%)"}
     * 또는 이전 리포트가 없으면 빈 map
     */
    @JsonProperty("report_history")
    private Map<String, String> reportHistory;
}
