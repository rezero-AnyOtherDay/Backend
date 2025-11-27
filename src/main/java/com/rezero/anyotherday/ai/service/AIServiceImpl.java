package com.rezero.anyotherday.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rezero.anyotherday.ai.dto.DiagnoseRequest;
import com.rezero.anyotherday.ai.dto.DiagnoseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * FastAPI AI 서버와 통신하는 서비스 구현
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ai.server.url:http://localhost:8000}")
    private String aiServerUrl;

    @Value("${ai.server.diagnose-endpoint:/diagnose}")
    private String diagnoseEndpoint;

    @Value("${ai.server.health-endpoint:/}")
    private String healthEndpoint;

    @Value("${ai.server.timeout:30000}")
    private long timeout;

    @Value("${ai.server.enabled:true}")
    private boolean aiEnabled;

    /**
     * FastAPI AI 서버에 진단 요청
     * 음성 파일 경로, 자가진단 결과, 이전 리포트 기록을 전송하고
     * AI 진단 결과를 받아온다
     */
    @Override
    public DiagnoseResponse diagnose(DiagnoseRequest request) {
        if (!aiEnabled) {
            log.warn("AI service is disabled");
            return createMockResponse();
        }

        try {
            log.info("AI diagnosis request started");
            log.info("  - Audio: {}", request.getAudioPath());
            log.info("  - Self Report: {}", request.getSelfReport());
            log.info("  - History Records: {}", request.getReportHistory().size());

            String url = aiServerUrl + diagnoseEndpoint;
            log.info("  - Target URL: {}", url);

            // HTTP 요청 생성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<DiagnoseRequest> entity = new HttpEntity<>(request, headers);

            // AI 서버 호출
            ResponseEntity<DiagnoseResponse> response = restTemplate.postForEntity(
                    url,
                    entity,
                    DiagnoseResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("AI diagnosis completed - Status: {}", response.getBody().getStatus());
                return response.getBody();
            } else {
                log.error("AI server response error - Status: {}", response.getStatusCode());
                throw new RuntimeException(
                        "AI server returned non-2xx status: " + response.getStatusCode()
                );
            }

        } catch (RestClientException e) {
            log.error("AI server connection failed", e);
            throw new RuntimeException(
                    "Failed to connect to AI server at " + aiServerUrl + ": " + e.getMessage(),
                    e
            );
        } catch (Exception e) {
            log.error("Error during AI diagnosis processing", e);
            throw new RuntimeException(
                    "Error during AI diagnosis: " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * AI 서버 상태 확인
     */
    @Override
    public String healthCheck() {
        if (!aiEnabled) {
            log.warn("AI service is disabled - skipping health check");
            return "AI service is disabled";
        }

        try {
            String url = aiServerUrl + healthEndpoint;
            log.info("Checking AI server status... URL: {}", url);

            ResponseEntity<String> response = restTemplate.getForEntity(
                    url,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("AI server is healthy: {}", response.getBody());
                return response.getBody();
            } else {
                log.warn("AI server response error - Status: {}", response.getStatusCode());
                return "AI server error: " + response.getStatusCode();
            }

        } catch (RestClientException e) {
            log.error("AI server connection failed: {}", e.getMessage());
            return "AI server unreachable: " + e.getMessage();
        }
    }

    /**
     * Mock 응답 생성 (AI 서버가 비활성화되었을 때 사용)
     */
    private DiagnoseResponse createMockResponse() {
        log.info("Returning mock AI response");
        return DiagnoseResponse.builder()
                .status("success")
                .result(objectMapper.createObjectNode()
                        .put("diagnosis", "Mock diagnosis result")
                        .put("confidence_score", 0.75)
                        .put("message", "This is a mock response (AI service disabled)"))
                .message("Mock response - AI service is disabled")
                .build();
    }
}
