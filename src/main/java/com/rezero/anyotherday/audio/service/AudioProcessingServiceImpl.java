package com.rezero.anyotherday.audio.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rezero.anyotherday.ai.dto.DiagnoseRequest;
import com.rezero.anyotherday.ai.dto.DiagnoseResponse;
import com.rezero.anyotherday.ai.service.AIService;
import com.rezero.anyotherday.ai.service.ReportHistoryService;
import com.rezero.anyotherday.audio.dto.AudioRecordDto;
import com.rezero.anyotherday.report.dto.ReportDto;
import com.rezero.anyotherday.report.service.ReportService;
import com.rezero.anyotherday.ward.dto.WardDto;
import com.rezero.anyotherday.ward.service.WardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 오디오 처리 서비스 구현
 * 오디오 업로드 후 AI 서버로 보내 진단 수행하고 리포트 생성
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AudioProcessingServiceImpl implements AudioProcessingService {
    private final AudioRecordService audioRecordService;
    private final WardService wardService;
    private final AIService aiService;
    private final ReportHistoryService reportHistoryService;
    private final ReportService reportService;
    private final ObjectMapper objectMapper;

    /**
     * 비동기 오디오 처리
     * 스프링의 @Async를 사용하여 별도 스레드에서 실행
     */
    @Override
    @Async("asyncExecutor")
    public void processAudioAsync(Integer recordId, Integer wardId) {
        log.info("[ASYNC] Audio processing started - recordId: {}, wardId: {}", recordId, wardId);
        try {
            processAudioInternal(recordId, wardId);
            log.info("[ASYNC] Audio processing completed");
        } catch (Exception e) {
            log.error("[ASYNC] Audio processing failed", e);
            handleProcessingError(recordId, e);
        }
    }

    /**
     * Synchronous audio processing (for testing)
     */
    @Override
    public void processAudioSync(Integer recordId, Integer wardId) {
        log.info("[SYNC] Audio processing started - recordId: {}, wardId: {}", recordId, wardId);
        try {
            processAudioInternal(recordId, wardId);
            log.info("[SYNC] Audio processing completed");
        } catch (Exception e) {
            log.error("[SYNC] Audio processing failed", e);
            handleProcessingError(recordId, e);
        }
    }

    /**
     * 내부 오디오 처리 로직
     * 1. 오디오 레코드 조회
     * 2. 피보호자 정보 조회
     * 3. 이전 리포트 히스토리 조회
     * 4. AI 서버 호출
     * 5. 리포트 저장
     */
    private void processAudioInternal(Integer recordId, Integer wardId) throws Exception {
        // Step 1: Fetch audio record
        log.info("Step 1: Fetching audio record...");
        AudioRecordDto audioRecord = audioRecordService.getRecordById(recordId);
        if (audioRecord == null) {
            throw new IllegalArgumentException("Audio record not found: " + recordId);
        }
        log.info("   Audio file: {}", audioRecord.getFileUrl());

        // Step 2: Fetch ward information
        log.info("Step 2: Fetching ward information...");
        WardDto ward = wardService.getWardById(wardId);
        if (ward == null) {
            throw new IllegalArgumentException("Ward not found: " + wardId);
        }
        log.info("   Name: {}, Age: {}, Gender: {}", ward.getName(), ward.getAge(), ward.getGender());

        // Step 3: Update status to processing
        log.info("Step 3: Updating status to processing...");
        audioRecordService.updateStatus(recordId, "processing", null);

        // Step 4: Fetch report history (for RAG)
        log.info("Step 4: Fetching report history...");
        Map<String, String> reportHistory = reportHistoryService.getReportHistory(wardId, 5);
        log.info("   Retrieved {} history records", reportHistory.size());
        for (Map.Entry<String, String> entry : reportHistory.entrySet()) {
            log.info("      - {}: {}", entry.getKey(), entry.getValue());
        }

        // Step 5: Parse diagnosis data
        log.info("Step 5: Parsing diagnosis data...");
        Map<String, Object> selfReport = parseDiagnosis(ward.getDiagnosis());
        log.info("   Diagnosis data: {}", selfReport);

        // Step 6: Call AI server
        log.info("Step 6: Calling AI server...");
        DiagnoseRequest aiRequest = DiagnoseRequest.builder()
                .audioPath(audioRecord.getFileUrl())
                .selfReport(selfReport)
                .reportHistory(reportHistory)
                .build();

        log.info("   Audio Path: {}", aiRequest.getAudioPath());
        log.info("   Self Report Keys: {}", selfReport.keySet());
        log.info("   History Size: {}", reportHistory.size());

        try {
            String requestJson = objectMapper.writeValueAsString(aiRequest);
            log.info("   Full Request: {}", requestJson);
        } catch (Exception e) {
            log.warn("Failed to log request: {}", e.getMessage());
        }

        DiagnoseResponse aiResponse = aiService.diagnose(aiRequest);

        if (aiResponse == null) {
            throw new RuntimeException("AI diagnosis failed: null response");
        }

        log.info("   AI diagnosis completed");
        log.info("   - Accuracy: {}", aiResponse.getAccuracy());
        log.info("   - Risk: {}", aiResponse.getRisk());
        log.info("   - Summary: {}", aiResponse.getSummary());

        // Step 7: Save report
        log.info("Step 7: Saving AI analysis result to report...");
        String analysisResultJson = objectMapper.writeValueAsString(aiResponse);

        ReportDto reportDto = ReportDto.builder()
                .recordId(recordId)
                .analysisResult(analysisResultJson)
                .build();

        ReportDto savedReport = reportService.createReport(reportDto);
        log.info("   Report saved successfully: reportId = {}", savedReport.getReportId());

        // Step 8: Update status to completed
        log.info("Step 8: Updating status to completed...");
        audioRecordService.updateStatus(recordId, "completed", null);

        log.info("Audio processing completed - recordId: {}, reportId: {}", recordId, savedReport.getReportId());
    }

    /**
     * Handle audio processing error
     */
    private void handleProcessingError(Integer recordId, Exception e) {
        try {
            String errorMessage = e.getMessage();
            log.error("Error message: {}", errorMessage);
            audioRecordService.updateStatus(recordId, "failed", errorMessage);
            log.info("Status updated to failed");
        } catch (Exception updateError) {
            log.error("Failed to update status: {}", updateError.getMessage());
        }
    }

    /**
     * Parse diagnosis JSON string to Map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseDiagnosis(String diagnosisJson) {
        try {
            if (diagnosisJson == null || diagnosisJson.isEmpty()) {
                log.warn("Diagnosis data is empty");
                return Map.of("answered", false);
            }

            Map<String, Object> diagnosis = objectMapper.readValue(diagnosisJson, Map.class);
            log.debug("Parsed diagnosis data: {}", diagnosis);
            return diagnosis;

        } catch (Exception e) {
            log.error("Failed to parse diagnosis data: {}", e.getMessage());
            return Map.of("answered", false);
        }
    }
}
