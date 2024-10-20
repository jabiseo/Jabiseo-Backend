package com.jabiseo.infra.s3;

import com.jabiseo.infra.client.NetworkApiErrorCode;
import com.jabiseo.infra.client.NetworkApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloudfront.url}")
    private String cloudFrontUrl;

    public String upload(MultipartFile file, String filePath) {
        try {
            String originalName = file.getOriginalFilename();
            String extension = StringUtils.getFilenameExtension(originalName);
            String uploadFileName = createFileName(filePath, extension);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(uploadFileName)
                    .contentType(extension)
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

            return cloudFrontUrl + uploadFileName;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new NetworkApiException(NetworkApiErrorCode.S3_UPLOAD_FAIL);
        }
    }


    private String createFileName(String filePath, String extension) {
        return filePath + UUID.randomUUID() + "." + extension;
    }

}
