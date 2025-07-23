package kr.co.loopz.object.service;

import kr.co.loopz.object.converter.ObjectConverter;
import kr.co.loopz.object.domain.ObjectEntity;
import kr.co.loopz.object.domain.ObjectImage;
import kr.co.loopz.object.dto.request.ObjectInfoRequest;
import kr.co.loopz.object.dto.response.DetailResponse;
import kr.co.loopz.object.dto.response.ObjectResponse;
import kr.co.loopz.object.dto.response.OrderObjectInfoResponse;
import kr.co.loopz.object.exception.ObjectException;
import kr.co.loopz.object.repository.LikeRepository;
import kr.co.loopz.object.repository.ObjectImageRepository;
import kr.co.loopz.object.repository.ObjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static kr.co.loopz.object.exception.ObjectErrorCode.INSUFFICIENT_STOCK;
import static kr.co.loopz.object.exception.ObjectErrorCode.OBJECT_ID_NOT_FOUND;

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

        List<String> imageUrls = getImageUrls(objectId);

        return objectConverter.toDetailResponse(entity, imageUrls, liked);
    }

    public List<ObjectResponse> getObjectListByIds(List<String> objectIds) {
        return objectIds.stream()
                .map(this::getObjectById)
                .toList();
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

    // 주문 시 상품 정보 반환
    public List<OrderObjectInfoResponse> getOrderObjectInfo(List<ObjectInfoRequest> requests) {

        return requests.stream()
                .map(req -> {
                    ObjectEntity object = findObjectEntity(req.objectId());

                    int stock = object.getDetail().getStock();
                    int quantity = req.quantity();

                    if (stock < quantity) {
                        throw new ObjectException(INSUFFICIENT_STOCK,
                                "재고 부족: objectId = " + req.objectId() + ", stock = " + stock + ", requested = " + quantity);
                    }

                    List<String> imageUrls = getImageUrls(req.objectId());

                    return objectConverter.toOrderObjectInfoResponse(object, imageUrls, req.quantity());
                })
                .toList();
    }

    public ObjectEntity findObjectEntity(String objectId) {
        return objectRepository.findByObjectId(objectId)
                .orElseThrow(() -> new ObjectException(OBJECT_ID_NOT_FOUND, "Object not found: " + objectId));
    }


    private List<String> getImageUrls(String objectId) {
        return objectImageRepository.findByObjectId(objectId).stream()
                .map(ObjectImage::getImageUrl)
                .collect(Collectors.toList());
    }

}
