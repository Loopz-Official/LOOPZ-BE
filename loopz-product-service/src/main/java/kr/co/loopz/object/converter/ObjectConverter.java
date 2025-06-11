package kr.co.loopz.object.converter;

import kr.co.loopz.object.domain.ObjectEntity;
import kr.co.loopz.object.dto.response.BoardResponse;
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
                        likeMap.getOrDefault(dto.objectId(), false)
                )).collect(Collectors.toList());
    }

    default BoardResponse toBoardResponse(List<ObjectResponse> dtos, Map<String, String> imageUrlMap, Map<String, Boolean> likeMap, boolean hasNext) {
        List<ObjectResponse> resultObjects = toObjectResponseList(dtos, imageUrlMap,likeMap);
        return new BoardResponse(resultObjects.size(), resultObjects, hasNext);
    }
}