package kr.co.loopz.admin.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import kr.co.loopz.admin.dto.response.UrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.amazonaws.HttpMethod;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String generatePresignedUrl(String fileName) {
        String folder = "images/object/";
        String uuidFileName = folder + UUID.randomUUID() + "_" + fileName;

        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 5);

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, uuidFileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);

        URL url = amazonS3Client.generatePresignedUrl(request);

        return url.toString();
    }

    public void deleteFile(String key) {
        log.info("Attempting to delete from S3. Bucket: {}, Key: {}", bucketName, key);

        try {
            boolean exists = amazonS3Client.doesObjectExist(bucketName, key);
            log.info("doesObjectExist returned: {}", exists);

            if (exists) {
                amazonS3Client.deleteObject(bucketName, key);
                log.info("S3 객체 삭제 완료: {}", key);
            } else {
                log.info("S3 객체가 존재하지 않음: {}", key);
            }
        } catch (Exception e) {
            log.error("S3 접근 중 예외 발생", e);
        }
    }
}