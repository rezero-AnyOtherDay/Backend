package com.rezero.anyotherday.ward.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WardDto {

    private Integer wardId;      // ward_id PK
    private Integer guardianId;  // guardian_id FK

    private String name;         // name
    @JsonProperty("birthDate")
    @JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate birthDate; // birth_date
    private Integer age;         // age
    private String gender;       // 'male' / 'female'
    private String phone;        // phone
    private String relationship; // relationship

    // MySQL JSON 컬럼 ↔ 자바 String (유효한 JSON 문자열이면 MySQL이 JSON으로 저장)
    private String diagnosis;    // diagnosis

    private LocalDateTime createdAt; // created_at
    private LocalDateTime updatedAt; // updated_at
    private String status;           // 'active' / 'deleted'
}
