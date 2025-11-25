package com.rezero.anyotherday.guardian.controller;

import com.rezero.anyotherday.guardian.dto.GuardianDto;
import com.rezero.anyotherday.guardian.service.GuardianService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/guardians")
@Tag(name = "Guardian", description = "보호자 회원가입·로그인 API")
public class GuardianController {

    private final GuardianService guardianService;

    // 1) 회원가입
    @Operation(summary = "보호자 회원가입")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody GuardianDto request) {
        try {
            GuardianDto result = guardianService.signup(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalStateException e) {
            // 이메일 중복 등
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 2) 로그인
    @Operation(summary = "보호자 로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email,
            @RequestParam String password) {
        try {
            GuardianDto guardian = guardianService.login(email, password);
            return ResponseEntity.ok(guardian);
        } catch (IllegalArgumentException e) {
            // 이메일 없음, 비밀번호 불일치 등
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
