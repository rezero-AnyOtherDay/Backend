package com.rezero.anyotherday.audio.service;

import com.rezero.anyotherday.audio.dao.AudioRecordDao;
import com.rezero.anyotherday.audio.dto.AudioRecordDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AudioRecordServiceImpl implements AudioRecordService {

    private final AudioRecordDao audioRecordDao;
    private final S3AudioStorageService s3AudioStorageService;

    @Override
    public AudioRecordDto uploadAndCreateRecord(Integer wardId,
            MultipartFile file,
            LocalDateTime recordedAt) {

        // 1) 확장자 추출
        String ext = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf(".") + 1); // ← . 제외하고 추출
        } else {
            ext = "bin"; // fallback
        }

        // 2) S3 저장 키 생성
        String key = "audio/" + wardId + "/" + UUID.randomUUID() + "." + ext;

        // 3) S3 업로드
        String fileUrl = s3AudioStorageService.uploadAudio(file, key);

        // 4) DTO 생성
        AudioRecordDto dto = new AudioRecordDto();
        dto.setWardId(wardId);
        dto.setRecordedAt(recordedAt);
        dto.setFileUrl(fileUrl);
        dto.setFileFormat(ext);
        dto.setStatus("pending");

        // 5) DB Insert
        audioRecordDao.createRecord(dto);

        // 6) Insert 된 값 조회해서 리턴
        return audioRecordDao.getRecordById(dto.getRecordId());
    }


    @Override
    public AudioRecordDto getRecordById(Integer recordId) {
        return audioRecordDao.getRecordById(recordId);
    }

    @Override
    public List<AudioRecordDto> getRecordsByWardId(Integer wardId) {
        return audioRecordDao.getRecordsByWardId(wardId);
    }

    @Override
    public AudioRecordDto getLatestRecordByWardId(Integer wardId) {
        return audioRecordDao.getLatestRecordByWardId(wardId);
    }

    @Override
    public void updateStatus(Integer recordId, String status, String message) {
        audioRecordDao.updateStatus(recordId, status, message);
    }
}