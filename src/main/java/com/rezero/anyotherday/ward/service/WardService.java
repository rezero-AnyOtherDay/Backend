package com.rezero.anyotherday.ward.service;

import com.rezero.anyotherday.ward.dto.WardDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

public interface WardService {
    // 1) Ward 생성
    WardDto createWard(WardDto wardDto);

    // 2) Ward 목록 조회
    List<WardDto> getWardsByGuardianId(int guardianId);


}
