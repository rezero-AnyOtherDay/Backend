package com.rezero.anyotherday.audio.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3AudioStorageServiceImpl implements S3AudioStorageService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket:audio-bucket}")
    private String bucket;

    @Override
    public String uploadAudio(MultipartFile file, String key) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(file.getSize());
            meta.setContentType(file.getContentType());

            log.info("Uploading file to S3: bucket={}, key={}, size={}", bucket, key, file.getSize());

            amazonS3.putObject(bucket, key, file.getInputStream(), meta);

            String url = amazonS3.getUrl(bucket, key).toString();
            log.info("File uploaded successfully: {}", url);

            return url;
        } catch (IOException e) {
            log.error("IOException occurred during S3 upload", e);
            throw new RuntimeException("S3 upload failed: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during S3 upload", e);
            throw new RuntimeException("S3 upload failed: " + e.getMessage(), e);
        }
    }
}