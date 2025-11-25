package com.rezero.anyotherday.ward.controller;

import com.rezero.anyotherday.ward.dto.WardDto;
import com.rezero.anyotherday.ward.service.WardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wards")
@Tag(name = "Ward", description = "피보호자 관리 API")
public class WardController {

    private final WardService wardService;

    // 1) 피보호자 생성
    @Operation(summary = "피보호자 등록")
    @PostMapping
    public ResponseEntity<?> createWard(@RequestBody WardDto request) {
        try {
            WardDto result = wardService.createWard(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
