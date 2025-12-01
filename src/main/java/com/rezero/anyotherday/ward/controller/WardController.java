package com.rezero.anyotherday.ward.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rezero.anyotherday.ward.dto.WardDto;
import com.rezero.anyotherday.ward.service.WardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wards")
@Tag(name = "Ward", description = "피보호자 관리 API")
public class WardController {

    private final WardService wardService;
    private final ObjectMapper objectMapper;

    // 1) 피보호자 생성
    @Operation(summary = "피보호자 등록")
    @PostMapping
    public ResponseEntity<?> createWard(@RequestBody WardDto request) {
        try {
            // 요청 데이터 로깅
            log.info("CreateWard request - name: {}, birthDate: {}, age: {}, gender: {}",
                request.getName(), request.getBirthDate(), request.getAge(), request.getGender());

            // birthDate 검증
            if (request.getBirthDate() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("생년월일(birthDate)은 필수 입력값입니다.");
            }

            WardDto result = wardService.createWard(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 2) Ward 목록 조회(guardianId 기준)
    @Operation(summary = "피보호자 목록조회")
    @GetMapping
    public ResponseEntity<List<WardDto>> getWards(@RequestParam int guardianId) {
        log.info("피보호자 목록 조회 - guardianId: {}", guardianId);
        List<WardDto> list = wardService.getWardsByGuardianId(guardianId);
        log.info("조회된 피보호자 개수: {}", list.size());
        log.info("피보호자 목록: {}", list);
        return ResponseEntity.ok(list);
    }

    // 3) 피보호자 단건 조회
    @Operation(summary = "피보호자 상세 조회")
    @GetMapping("/{wardId}")
    public ResponseEntity<?> getWard(@PathVariable int wardId) {
        WardDto ward = wardService.getWardById(wardId);
        if (ward == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("존재하지 않는 피보호자입니다.");
        }
        return ResponseEntity.ok(ward);
    }

    // 4) 자가진단(JSON) 수정
    @Operation(summary = "피보호자 자가진단 수정")
    @PutMapping("/{wardId}/diagnosis")
    public ResponseEntity<?> updateDiagnosis(
            @PathVariable int wardId,
            @RequestBody Map<String, Object> diagnosisBody
    ) {
        try {
            // 요청 JSON 전체를 그대로 문자열로 직렬화
            String diagnosisJson = objectMapper.writeValueAsString(diagnosisBody);

            WardDto result = wardService.updateDiagnosis(wardId, diagnosisJson);
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("유효한 JSON 형식이 아닙니다.");
        }
    }
}



