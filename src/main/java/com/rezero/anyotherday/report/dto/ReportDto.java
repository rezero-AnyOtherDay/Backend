package com.rezero.anyotherday.report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDto {

    @JsonProperty("report_id")
    private Integer reportId;

    @JsonProperty("record_id")
    private Integer recordId;

    @JsonProperty("analysis_result")
    private String analysisResult;  // JSON 문자열로 저장

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
