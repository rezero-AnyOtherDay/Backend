package com.rezero.anyotherday.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class S3Config {

    @Value("${cloud.aws.s3.access-key:}")
    private String accessKey;

    @Value("${cloud.aws.s3.secret-key:}")
    private String secretKey;

    @Value("${cloud.aws.s3.region}")
    private String region;

    @Value("${cloud.aws.s3.bucket:}")
    private String bucket;

    /**
     * AWS S3가 설정된 경우에만 Bean 생성
     * cloud.aws.s3.access-key가 설정되어 있으면 활성화
     */
    @Bean
    @Primary
    @ConditionalOnProperty(name = "cloud.aws.s3.access-key")
    public AmazonS3 amazonS3Client() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }

    /**
     * S3가 설정되지 않은 경우 더미 S3 서비스 사용 (로컬 개발용)
     */
    @Bean
    @ConditionalOnProperty(name = "cloud.aws.s3.access-key", havingValue = "", matchIfMissing = true)
    public AmazonS3 dummyS3Client() {
        return AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                "http://localhost:9000",
                                "us-east-1"
                        )
                )
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials("minioadmin", "minioadmin")
                ))
                .withPathStyleAccessEnabled(true)
                .build();
    }
}
