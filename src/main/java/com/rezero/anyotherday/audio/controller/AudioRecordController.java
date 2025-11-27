package com.rezero.anyotherday.audio.controller;

import com.rezero.anyotherday.audio.dto.AudioRecordDto;
import com.rezero.anyotherday.audio.service.AudioProcessingService;
import com.rezero.anyotherday.audio.service.AudioRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/audio-records")
@RequiredArgsConstructor
@Tag(name = "Audio Record", description = "오디오 녹음 업로드 및 관리 API")
public class AudioRecordController {

    private final AudioRecordService audioRecordService;
    private final AudioProcessingService audioProcessingService;

    @Operation(summary = "오디오 파일 업로드")
    @PostMapping( value = "/ward/{wardId}",
                  consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> uploadAudioRecord(
            @PathVariable Integer wardId,
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "recordedAt", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate recordedDate
    ) {
        try {
            log.info("Uploading audio record for wardId: {}", wardId);

            LocalDateTime recordedAt = recordedDate != null
                    ? recordedDate.atStartOfDay()
                    : null;

            AudioRecordDto result = audioRecordService.uploadAndCreateRecord(wardId, file, recordedAt);

            // Start async AI processing (audio analysis and report generation)
            log.info("Starting async AI processing - recordId: {}", result.getRecordId());
            audioProcessingService.processAudioAsync(result.getRecordId(), wardId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);
            response.put("message", "Audio record uploaded successfully. AI analysis in progress...");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error uploading audio record", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Upload failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "오디오 레코드 조회")
    @GetMapping("/{recordId}")
    public ResponseEntity<Map<String, Object>> getRecordById(@PathVariable Integer recordId) {
        try {
            AudioRecordDto record = audioRecordService.getRecordById(recordId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", record);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch record");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "피보호자의 오디오 레코드 목록 조회")
    @GetMapping("/ward/{wardId}")
    public ResponseEntity<Map<String, Object>> getRecordsByWard(@PathVariable Integer wardId) {
        try {
            List<AudioRecordDto> records = audioRecordService.getRecordsByWardId(wardId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", records);
            response.put("count", records.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch records");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "피보호자의 최신 오디오 레코드 조회")
    @GetMapping("/ward/{wardId}/latest")
    public ResponseEntity<Map<String, Object>> getLatestRecordByWard(@PathVariable Integer wardId) {
        try {
            AudioRecordDto record = audioRecordService.getLatestRecordByWardId(wardId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", record);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch latest record");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "오디오 레코드 동기 처리 (테스트용)")
    @PostMapping("/{recordId}/process-sync")
    public ResponseEntity<Map<String, Object>> processAudioSync(
            @PathVariable Integer recordId,
            @RequestParam Integer wardId
    ) {
        try {
            log.info("Manual sync audio processing triggered - recordId: {}, wardId: {}", recordId, wardId);

            audioProcessingService.processAudioSync(recordId, wardId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Audio processing completed");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing audio", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Processing failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}