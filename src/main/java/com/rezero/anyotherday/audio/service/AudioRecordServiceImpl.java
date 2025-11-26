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

        String ext = "";
        if (file.getOriginalFilename() != null &&
                file.getOriginalFilename().contains(".")) {
            ext = file.getOriginalFilename()
                    .substring(file.getOriginalFilename().lastIndexOf("."));
        }

        String key = "audio/" + wardId + "/" + UUID.randomUUID() + ext;

        String fileUrl = s3AudioStorageService.uploadAudio(file, key);

        AudioRecordDto dto = new AudioRecordDto();
        dto.setWardId(wardId);
        dto.setRecordedAt(recordedAt);
        dto.setFileUrl(fileUrl);
        dto.setStatus("uploaded");

        audioRecordDao.createRecord(dto);

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