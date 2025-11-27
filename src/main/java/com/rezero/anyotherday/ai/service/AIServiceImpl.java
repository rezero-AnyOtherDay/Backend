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

            // 요청 데이터를 JSON 문자열로 로깅
            try {
                String requestJson = objectMapper.writeValueAsString(request);
                log.info("  - Request Body: {}", requestJson);
            } catch (Exception e) {
                log.warn("Failed to serialize request for logging: {}", e.getMessage());
            }

            // HTTP 요청 생성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<DiagnoseRequest> entity = new HttpEntity<>(request, headers);

            // AI 서버 호출 - 원본 응답을 String으로 먼저 받기
            ResponseEntity<String> rawResponse = restTemplate.postForEntity(
                    url,
                    entity,
                    String.class
            );

            log.info("AI server response status: {}", rawResponse.getStatusCode());
            log.info("AI server raw response body: {}", rawResponse.getBody());

            if (rawResponse.getStatusCode().is2xxSuccessful()) {
                // 원본 응답을 DiagnoseResponse로 파싱
                try {
                    DiagnoseResponse diagnoseResponse = objectMapper.readValue(
                            rawResponse.getBody(),
                            DiagnoseResponse.class
                    );
                    log.info("Parsed DiagnoseResponse: {}", diagnoseResponse);
                    log.info("  - accuracy: {}", diagnoseResponse.getAccuracy());
                    log.info("  - asr: {}", diagnoseResponse.getAsr());
                    log.info("  - risk: {}", diagnoseResponse.getRisk());
                    log.info("  - explain: {}", diagnoseResponse.getExplain());
                    log.info("  - summary: {}", diagnoseResponse.getSummary());

                    // AI 서버 응답에 accuracy가 있으면 성공
                    if (diagnoseResponse.getAccuracy() != null && !diagnoseResponse.getAccuracy().isEmpty()) {
                        log.info("AI diagnosis completed successfully");
                        return diagnoseResponse;
                    } else {
                        log.error("AI diagnosis response is missing required fields");
                        throw new RuntimeException(
                                "AI diagnosis failed: Missing required fields (accuracy)"
                        );
                    }
                } catch (Exception parseException) {
                    log.error("Failed to parse AI response: {}", parseException.getMessage());
                    throw new RuntimeException("Failed to parse AI response: " + parseException.getMessage());
                }
            } else {
                log.error("AI server response error - Status: {}", rawResponse.getStatusCode());
                log.error("Response body: {}", rawResponse.getBody());
                throw new RuntimeException(
                        "AI server returned non-2xx status: " + rawResponse.getStatusCode()
                );
            }

        } catch (RestClientException e) {
            log.error("AI server connection failed: {}", e.getMessage(), e);
            throw new RuntimeException(
                    "Failed to connect to AI server at " + aiServerUrl + ": " + e.getMessage(),
                    e
            );
        } catch (Exception e) {
            log.error("Error during AI diagnosis processing: {}", e.getMessage(), e);
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
                .accuracy(java.util.List.of(45.0, 30.0, 25.0))
                .asr("Mock ASR 결과 - 테스트 모드에서 생성된 대화 기록입니다.")
                .risk(java.util.List.of("뇌졸중", "치매"))
                .explain(java.util.List.of(
                        "뇌졸중 의심: 45% 확률로 위험합니다.",
                        "치매 의심: 30% 확률로 위험합니다.",
                        "파킨슨: 10% 확률입니다.",
                        "루게릭: 5% 확률입니다."
                ))
                .summary("종합 소견: 테스트 모드에서 생성된 목 응답입니다. 실제 AI 서버의 응답을 기다리고 있습니다.")
                .build();
    }
}
