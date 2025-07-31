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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static kr.co.loopz.object.exception.ObjectErrorCode.DUPLICATE_OBJECT_NAME;
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

        if (!userClient.existsByUserId(userId)) {
            throw new ObjectException(USER_ID_NOT_FOUND, "User with ID not found: " + userId);
        }

        if (objectRepository.existsByObjectName(request.objectName())) {
            throw new ObjectException(DUPLICATE_OBJECT_NAME, "Object name: " + request.objectName());
        }

        // 상세 정보 생성
        ObjectDetail detail = ObjectDetail.upload(
                request.size(),
                request.descriptionUrl(),
                request.stock()
        );

        // 객체 엔티티 생성
        ObjectEntity objectEntity = ObjectEntity.upload(
                request.objectName(),
                request.objectPrice(),
                request.intro(),
                request.objectType(),
                request.objectSize(),
                new HashSet<>(request.keywords()),
                detail
        );

        objectRepository.save(objectEntity);

        // 이미지 URL 생성
        String cdnDomain = "https://static.loopz.co.kr/";
        String imageKey = request.imageKey();
        String imageUrl = cdnDomain + imageKey;

        ObjectImage objectImage = ObjectImage.builder()
                .objectId(objectEntity.getObjectId())
                .imageUrl(imageUrl)
                .build();
        objectImageRepository.save(objectImage);

        InternalUploadResponse response = objectConverter.toInternalUploadResponse(objectEntity, imageUrl);
        return response;
    }
}