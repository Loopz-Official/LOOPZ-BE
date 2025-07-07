package kr.co.loopz.object.service;

import kr.co.loopz.common.domain.Image;
import kr.co.loopz.object.Exception.ObjectException;
import kr.co.loopz.object.converter.ObjectConverter;
import kr.co.loopz.object.domain.ObjectEntity;
import kr.co.loopz.object.domain.ObjectImage;
import kr.co.loopz.object.dto.response.DetailResponse;
import kr.co.loopz.object.dto.response.ObjectResponse;
import kr.co.loopz.object.repository.LikeRepository;
import kr.co.loopz.object.repository.ObjectImageRepository;
import kr.co.loopz.object.repository.ObjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static kr.co.loopz.object.Exception.ObjectErrorCode.OBJECT_ID_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObjectDetailService {

    private final ObjectRepository objectRepository;
    private final ObjectImageRepository objectImageRepository;
    private final LikeRepository likeRepository;

    private final ObjectConverter objectConverter;

    public DetailResponse getObjectDetails(String userId, String objectId) {
        ObjectEntity entity = findObjectEntity(objectId);

        boolean liked = false;
        if (userId != null) {
            liked = likeRepository.existsByUserIdAndObjectId(userId, objectId);
        }

        List<String> imageUrls = objectImageRepository.findByObjectId(objectId).stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        return objectConverter.toDetailResponse(entity, imageUrls, liked);
    }


    public ObjectResponse getObjectById(String objectId) {

        ObjectEntity entity = findObjectEntity(objectId);

        List<ObjectImage> images = objectImageRepository.findByObjectId(objectId);
        String firstImageUrl = images.isEmpty() ? "" : images.get(0).getImageUrl();

        return objectConverter.toObjectResponse(entity, firstImageUrl);
    }

    @Transactional
    public void decreaseStock(String objectId, int quantity) {
        ObjectEntity object = findObjectEntity(objectId);
        object.getDetail().decreaseStock(quantity);
    }

    public int getStock(String objectId) {
        ObjectEntity object = findObjectEntity(objectId);
        return object.getDetail().getStock();
    }

    private ObjectEntity findObjectEntity(String objectId) {
        return objectRepository.findByObjectId(objectId)
                .orElseThrow(() -> new ObjectException(OBJECT_ID_NOT_FOUND, "Object not found: " + objectId));
    }

}
