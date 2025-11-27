package com.rezero.anyotherday.audio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * audio_record 테이블과 매핑되는 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AudioRecordDto {

    private Integer recordId;
    private Integer wardId;

    private LocalDateTime uploadedAt;
    private LocalDateTime recordedAt;

    private String fileUrl;

    private String status;
    private String errorMessage;
    private String fileFormat;
    private String transcriptText;
}