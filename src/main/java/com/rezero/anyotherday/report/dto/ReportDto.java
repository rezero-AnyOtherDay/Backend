package com.rezero.anyotherday.report.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private Integer reportId;
    private Integer recordId;
    private String analysisResult;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
