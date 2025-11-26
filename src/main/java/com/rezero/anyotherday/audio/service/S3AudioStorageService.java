package com.rezero.anyotherday.audio.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3AudioStorageService {
    String uploadAudio(MultipartFile file, String key);
}