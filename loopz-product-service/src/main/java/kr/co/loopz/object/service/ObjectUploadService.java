package kr.co.loopz.object.service;

import kr.co.loopz.object.client.UserClient;
import kr.co.loopz.object.converter.ObjectConverter;
import kr.co.loopz.object.domain.ObjectDetail;
import kr.co.loopz.object.domain.ObjectEntity;
import kr.co.loopz.object.domain.ObjectImage;
import kr.co.loopz.object.dto.request.InternalUploadRequest;
import kr.co.loopz.object.dto.response.InternalUploadResponse;
import kr.co.loopz.object.exception.ObjectException;
import kr.co.loopz.object.repository.ObjectImageRepository;
import kr.co.loopz.object.repository.ObjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static kr.co.loopz.object.exception.ObjectErrorCode.USER_ID_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class ObjectUploadService {

    private final ObjectRepository objectRepository;
    private final ObjectImageRepository objectImageRepository;
    private final ObjectConverter objectConverter;
    private final UserClient userClient;

    @Transactional
    public InternalUploadResponse uploadObject(String userId, InternalUploadRequest request) {

        validateUserExists(userId);

        // 상세 정보 생성
        ObjectDetail detail = createObjectDetail(request);

        // 객체 엔티티 생성
        ObjectEntity objectEntity = createObjectEntity(request,detail);

        // 이미지 URL 생성
        ObjectImage objectImage = createAndSaveObjectImage(objectEntity.getObjectId(), request.imageKey());

        return objectConverter.toInternalUploadResponse(objectEntity, objectImage.getImageUrl());
    }

    private void validateUserExists(String userId) {
        if (!userClient.existsByUserId(userId)) {
            throw new ObjectException(USER_ID_NOT_FOUND, "User with ID not found: " + userId);
        }
    }

    private ObjectDetail createObjectDetail(InternalUploadRequest request) {
        return ObjectDetail.upload(
                request.size(),
                request.descriptionUrl(),
                request.stock()
        );
    }

    private ObjectEntity createObjectEntity(InternalUploadRequest request, ObjectDetail detail) {
        ObjectEntity entity = ObjectEntity.upload(
                request.objectName(),
                request.objectPrice(),
                request.intro(),
                request.objectType(),
                request.objectSize(),
                new HashSet<>(request.keywords()),
                detail
        );
        return objectRepository.save(entity);
    }

    private ObjectImage createAndSaveObjectImage(String objectId, String imageKey) {
        String cdnDomain = "https://static.loopz.co.kr/";
        String imageUrl = cdnDomain + imageKey;

        ObjectImage image = ObjectImage.builder()
                .objectId(objectId)
                .imageUrl(imageUrl)
                .build();
        return objectImageRepository.save(image);
    }
}