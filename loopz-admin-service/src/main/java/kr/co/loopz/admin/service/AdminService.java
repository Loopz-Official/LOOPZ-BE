package kr.co.loopz.admin.service;

import feign.FeignException;
import kr.co.loopz.admin.client.ProductClient;
import kr.co.loopz.admin.dto.request.UploadRequest;
import kr.co.loopz.admin.dto.response.UploadResponse;
import kr.co.loopz.admin.exception.AdminException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

import static kr.co.loopz.admin.exception.AdminErrorCode.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final ProductClient productClient;

    public UploadResponse uploadObject(String userId, UploadRequest request) {

        return handleFeignCall(() -> productClient.uploadObject(userId, request), request.objectName(),false);

    }

    public UploadResponse modifyObject(String userId, String objectId, UploadRequest request) {

        return handleFeignCall(() -> productClient.modifyObject(userId, objectId, request), request.objectName(),true);

    }


    private <T> T handleFeignCall(Supplier<T> feignCall, String objectName, boolean isModify) {
        try {
            return feignCall.get();
        } catch (FeignException.Conflict e) {
            throw new AdminException(DUPLICATE_OBJECT_NAME, "objectName:" + objectName);
        } catch (FeignException.BadRequest e) {
            log.error("Feign 요청 중 BadRequest 발생: {}", e.contentUTF8(), e);

            if (isModify) {
                throw new AdminException(CANNOT_FIND_OBJECT, "objectId 없음");
            }

            throw new AdminException(INVALID_REQUEST, "Invalid request");
        }
    }

}
