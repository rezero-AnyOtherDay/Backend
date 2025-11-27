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
     *   "survey": {
     *     "질문1": 3,
     *     "질문2": 1,
     *     ...
     *   }
     * }
     */
    @JsonProperty("self_report")
    private Map<String, Object> selfReport;

    /**
     * 이전 리포트 기록 요약 (RAG용)
     * 예: ["2025-11-20: 인지기능 저하 패턴 감지", "2025-11-15: 정상"]
     */
    @JsonProperty("report_history")
    private List<String> reportHistory;
}
