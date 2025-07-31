package kr.co.loopz.admin.service;

import kr.co.loopz.admin.client.ProductClient;
import kr.co.loopz.admin.dto.request.UploadRequest;
import kr.co.loopz.admin.dto.response.UploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final ProductClient productClient;

    public UploadResponse uploadObject(String userId,UploadRequest request) {
        return productClient.uploadObject(userId, request);
    }

}
