package com.rezero.anyotherday.guardian.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuardianDto {
    private Integer guardianId;   // guardian_id
    private String name;          // name
    private String email;         // email
    private String password;      // password
    private String phone;         // phone

    private LocalDateTime createdAt; // created_at
    private LocalDateTime updatedAt; // updated_at

    private String status;        // status : 'active' / 'deleted'
}

