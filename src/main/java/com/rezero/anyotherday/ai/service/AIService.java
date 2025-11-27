package com.rezero.anyotherday.ai.service;

import com.rezero.anyotherday.ai.dto.DiagnoseRequest;
import com.rezero.anyotherday.ai.dto.DiagnoseResponse;

/**
 * AI 서버 호출 서비스 인터페이스
 */
public interface AIService {
    /**
     * FastAPI AI 서버에 진단 요청
     *
     * @param request 진단 요청 (audio_path, self_report, report_history 포함)
     * @return AI 진단 결과
     * @throws RuntimeException AI 서버 호출 실패 시
     */
    DiagnoseResponse diagnose(DiagnoseRequest request);

    /**
     * AI 서버 상태 확인
     *
     * @return 서버 상태 메시지
     */
    String healthCheck();
}
