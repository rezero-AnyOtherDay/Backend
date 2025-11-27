package com.rezero.anyotherday.audio.service;

/**
 * 오디오 처리 서비스 (AI 호출)
 * 오디오 업로드 후 비동기로 AI 분석 수행
 */
public interface AudioProcessingService {
    /**
     * 오디오 파일을 AI 서버로 보내 진단 수행
     * 비동기 처리: 결과는 데이터베이스에 자동 저장
     *
     * @param recordId 음성 레코드 ID
     * @param wardId 피보호자 ID
     */
    void processAudioAsync(Integer recordId, Integer wardId);

    /**
     * 동기 처리 (테스트용)
     *
     * @param recordId 음성 레코드 ID
     * @param wardId 피보호자 ID
     */
    void processAudioSync(Integer recordId, Integer wardId);
}
