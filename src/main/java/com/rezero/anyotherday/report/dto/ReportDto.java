package com.rezero.anyotherday.report.dto;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private Integer reportId;
    private Integer recordId;
    private String analysisResult;  // AI 분석 결과 JSON
    private String summary;         // 과거~현재 200자 요약 (이전 대화 요약)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}