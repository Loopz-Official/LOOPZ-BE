package kr.co.loopz.object.converter;

import kr.co.loopz.object.domain.ObjectDetail;
import kr.co.loopz.object.domain.ObjectEntity;
import kr.co.loopz.object.dto.response.*;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ObjectConverter {

    //Product 엔티티를 ObjectResponseDTO로
    ObjectResponse toObjectResponse(ObjectEntity entity);
    ObjectResponse toObjectResponse(ObjectEntity entity, String imageUrl);

    // List<Product> -> List<ObjectResponse>
    List<ObjectResponse> toObjectResponseList(List<ObjectEntity> objectEntities);
    // 찜 여부와 image url 반영해 새로운 DTO 리스트 반환
    default List<ObjectResponse> toObjectResponseList(List<ObjectResponse> dtos, Map<String, String> imageUrlMap,Map<String, Boolean> likeMap) {
        return dtos.stream()
                .map(dto -> new ObjectResponse(
                        dto.objectId(),
                        dto.objectName(),
                        dto.intro(),
                        imageUrlMap.get(dto.objectId()),
                        dto.objectPrice(),
                        likeMap.getOrDefault(dto.objectId(), false),
                        dto.stock()
                )).collect(Collectors.toList());
    }

    default BoardResponse toBoardResponse(int itemTotalSize ,List<ObjectResponse> dtos, Map<String, String> imageUrlMap, Map<String, Boolean> likeMap, boolean hasNext) {
        List<ObjectResponse> resultObjects = toObjectResponseList(dtos, imageUrlMap,likeMap);
        return new BoardResponse(itemTotalSize, resultObjects, hasNext);
    }

    default DetailResponse toDetailResponse(ObjectEntity entity, List<String> imageUrls, Boolean liked) {


            ObjectDetail detail = entity.getDetail();
            return new DetailResponse(
                    entity.getObjectId(),
                    entity.getObjectName(),
                    entity.getIntro(),
                    imageUrls.isEmpty() ? null : imageUrls.get(0),
                    entity.getObjectPrice(),
                    liked,
                    detail.getStock(),
                    detail.getSize(),
                    detail.getDescriptionUrl()
            );
        }

    default List<ObjectNameResponse> toObjectNameResponseList(List<ObjectEntity> entities) {
        return entities.stream()
                .map(entity -> new ObjectNameResponse(entity.getObjectId(), entity.getObjectName()))
                .collect(Collectors.toList());
    }

    default OrderObjectInfoResponse toOrderObjectInfoResponse(ObjectEntity object, List<String> imageUrls, int quantity) {
        String firstImageUrl = imageUrls.isEmpty() ? "" : imageUrls.get(0);

        return new OrderObjectInfoResponse(
                object.getObjectId(),
                object.getObjectName(),
                firstImageUrl,
                object.getObjectPrice(),
                quantity,
                object.getDetail().getStock()
        );
    }

    default ObjectLikedResponse toLikedResponse(String objectId, boolean liked) {
        return new ObjectLikedResponse(objectId, liked);
    }

}
