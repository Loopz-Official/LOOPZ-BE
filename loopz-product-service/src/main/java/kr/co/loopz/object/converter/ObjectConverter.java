package kr.co.loopz.object.converter;

import kr.co.loopz.object.domain.ObjectDetail;
import kr.co.loopz.object.domain.ObjectEntity;
import kr.co.loopz.object.dto.response.BoardResponse;
import kr.co.loopz.object.dto.response.DetailResponse;
import kr.co.loopz.object.dto.response.ObjectResponse;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ObjectConverter {

    //Product 엔티티를 ObjectResponseDTO로
    ObjectResponse toObjectResponse(ObjectEntity entity);

    // List<Product> -> List<ObjectResponse>
    List<ObjectResponse> toObjectResponseList(List<ObjectEntity> objectEntities);

    default ObjectResponse toObjectResponse(ObjectEntity entity, String imageUrl) {
        ObjectResponse baseDto = toObjectResponse(entity);
        return new ObjectResponse(
                baseDto.objectId(),
                baseDto.objectName(),
                baseDto.intro(),
                imageUrl,
                baseDto.objectPrice(),
                baseDto.soldOut(),
                baseDto.liked(),
                baseDto.stock()
        );
    }


    // 찜 여부와 image url 반영해 새로운 DTO 리스트 반환
    default List<ObjectResponse> toObjectResponseList(List<ObjectResponse> dtos, Map<String, String> imageUrlMap,Map<String, Boolean> likeMap) {
        return dtos.stream()
                .map(dto -> new ObjectResponse(
                        dto.objectId(),
                        dto.objectName(),
                        dto.intro(),
                        imageUrlMap.get(dto.objectId()),
                        dto.objectPrice(),
                        dto.soldOut(),
                        likeMap.getOrDefault(dto.objectId(), false),
                        dto.stock()
                )).collect(Collectors.toList());
    }

    default BoardResponse toBoardResponse(int itemTotalSize ,List<ObjectResponse> dtos, Map<String, String> imageUrlMap, Map<String, Boolean> likeMap, boolean hasNext) {
        List<ObjectResponse> resultObjects = toObjectResponseList(dtos, imageUrlMap,likeMap);
        return new BoardResponse(itemTotalSize, resultObjects, hasNext);
    }

    default DetailResponse toDetailResponse(ObjectEntity entity, List<String> imageUrls, Boolean liked) {
        ObjectResponse objectResponse = new ObjectResponse(
                entity.getObjectId(),
                entity.getObjectName(),
                entity.getIntro(),
                imageUrls.isEmpty() ? null : imageUrls.get(0),
                entity.getObjectPrice(),
                entity.isSoldOut(),
                liked,
                entity.getStock()
        );

        ObjectDetail detail = entity.getDetail();
        return new DetailResponse(
                objectResponse,
                detail.getSize(),
                detail.getDescriptionUrl(),
                detail.getStock()
        );
    }

}