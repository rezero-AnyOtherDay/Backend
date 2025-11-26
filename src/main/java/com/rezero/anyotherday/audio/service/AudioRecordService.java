package com.rezero.anyotherday.audio.service;

import com.rezero.anyotherday.audio.dto.AudioRecordDto;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface AudioRecordService {

    AudioRecordDto uploadAndCreateRecord(Integer wardId,
            MultipartFile file,
            LocalDateTime recordedAt);

    AudioRecordDto getRecordById(Integer recordId);

    List<AudioRecordDto> getRecordsByWardId(Integer wardId);

    AudioRecordDto getLatestRecordByWardId(Integer wardId);

    void updateStatus(Integer recordId, String status, String errorMessage);
}