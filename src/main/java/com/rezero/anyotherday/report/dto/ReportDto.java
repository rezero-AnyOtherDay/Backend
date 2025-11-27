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
    private String analysisResult;  // VO 클래스로 타입 안정성 확보
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}