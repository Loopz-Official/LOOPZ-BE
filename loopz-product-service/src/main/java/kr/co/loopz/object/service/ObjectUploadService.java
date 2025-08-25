package kr.co.loopz.object.service;

import kr.co.loopz.object.client.S3Client;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

import static kr.co.loopz.object.exception.ObjectErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ObjectUploadService {

    @Value("${cdn-domain}")
    private String cdnDomain;

    private final ObjectRepository objectRepository;
    private final ObjectImageRepository objectImageRepository;
    private final ObjectConverter objectConverter;
    private final UserClient userClient;
    private final S3Client s3Client;

    /**
     * 신규 객체 업로드 처리
     * - 사용자 존재 검증
     * - 상세 정보 생성
     * - 객체 엔티티 생성 및 저장
     * - 이미지 URL 생성 및 저장
     */
    @Transactional
    public InternalUploadResponse uploadObject(String userId, InternalUploadRequest request) {

        validateUserExists(userId);

        // 상세 정보 생성
        ObjectDetail detail = createObjectDetail(request);

        // 객체 엔티티 생성
        ObjectEntity objectEntity = createObjectEntity(request, detail);

        // 이미지 URL 생성
        ObjectImage objectImage = createAndSaveObjectImage(objectEntity.getObjectId(), request.imageKey());

        return objectConverter.toInternalUploadResponse(objectEntity, objectImage.getImageUrl());
    }

    /**
     * 기존 객체 수정 처리
     * - 사용자 존재 검증
     * - 객체 조회 및 수정
     * - 이미지 URL 수정 및 저장
     */
    @Transactional
    public InternalUploadResponse modifyObject(String userId, String objectId, InternalUploadRequest request) {

        validateUserExists(userId);

        ObjectEntity objectEntity = findAndModifyObject(objectId, request);

        ObjectImage objectImage = createAndSaveObjectImage(objectEntity.getObjectId(), request.imageKey());

        return objectConverter.toInternalUploadResponse(objectEntity, objectImage.getImageUrl());
    }

    /**
     * 기존 객체 수정 처리
     * - 사용자 존재 검증
     * - 상품 존재 검증
     * - 이미지 함꼐 삭제
     */
    @Transactional
    public void deleteObject(String userId, String objectId) {

        validateUserExists(userId);

        ObjectEntity objectEntity = validateObjectExists(objectId);

        objectImageRepository.findByObjectId(objectId)
                .ifPresent(image -> {
                    try {

                        String s3Key = image.getS3Key();

                        log.info("Deleting S3 key: {}", s3Key);
                        // S3 삭제
                        s3Client.deleteFile(s3Key);
                        // DB 삭제
                        objectImageRepository.delete(image);

                    } catch (Exception e) {
                        throw new RuntimeException("S3 이미지 삭제 실패: " , e);
                    }
                });
        objectRepository.delete(objectEntity);
    }


    private void validateUserExists(String userId) {
        if (!userClient.existsByUserId(userId)) {
            throw new ObjectException(USER_ID_NOT_FOUND, "User with ID not found: " + userId);
        }
    }

    private ObjectEntity validateObjectExists(String objectId) {
        return objectRepository.findByObjectId(objectId)
                .orElseThrow(() -> new ObjectException(OBJECT_NOT_FOUND, "Object not found: " + objectId));
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
        String imageUrl = cdnDomain + imageKey;
        String s3Key = imageKey.startsWith("/") ? imageKey.substring(1) : imageKey;

        Optional<ObjectImage> exists = objectImageRepository.findByObjectId(objectId);
        ObjectImage image = exists
                .map(img -> img.replaceImage(imageUrl))
                .orElseGet(() -> ObjectImage.builder()
                        .objectId(objectId)
                        .imageUrl(imageUrl)
                        .s3Key(s3Key)
                        .build());
        return objectImageRepository.save(image);
    }

    private ObjectEntity findAndModifyObject(String objectId, InternalUploadRequest request) {

        ObjectEntity objectEntity = objectRepository.findByObjectId(objectId)
                .orElseThrow(() -> new ObjectException(OBJECT_NOT_FOUND, "Object not found: " + objectId));

        ObjectDetail detail = createObjectDetail(request);

        objectEntity.modify(
                request.objectName(),
                request.objectPrice(),
                request.intro(),
                request.objectType(),
                request.objectSize(),
                new HashSet<>(request.keywords()),
                detail
        );

        return objectEntity;
    }

}