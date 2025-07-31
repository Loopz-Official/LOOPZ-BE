package kr.co.loopz.admin.service;

import feign.FeignException;
import kr.co.loopz.admin.client.ProductClient;
import kr.co.loopz.admin.dto.request.UploadRequest;
import kr.co.loopz.admin.dto.response.UploadResponse;
import kr.co.loopz.admin.exception.AdminException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static kr.co.loopz.admin.exception.AdminErrorCode.DUPLICATE_OBJECT_NAME;


@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final ProductClient productClient;

    public UploadResponse uploadObject(String userId, UploadRequest request) {
        try {
            return productClient.uploadObject(userId, request);
        } catch (FeignException.BadRequest e) {

            log.error("상품 업로드 중 에러 발생: {}", e.contentUTF8(), e);

            throw new AdminException(DUPLICATE_OBJECT_NAME, "objectName:"+request.objectName());
        }
    }

}
