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
        String uuidFileName = folder+ UUID.randomUUID() + "_" + fileName;

        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 5);

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, uuidFileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);

        URL url = amazonS3Client.generatePresignedUrl(request);

        return url.toString();
    }
}
