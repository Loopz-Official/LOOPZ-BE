package kr.co.loopz.converter;

import kr.co.loopz.domain.Product;
import kr.co.loopz.dto.response.BoardResponse;
import kr.co.loopz.dto.response.ObjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ObjectConverter {

    //Product 엔티티를 ObjectResponseDTO로
    ObjectResponse toObjectResponse(Product entity);

    // List<Product> -> List<ObjectResponse>
    List<ObjectResponse> toObjectResponseList(List<Product> products);


    // 찜 여부 반영해 새로운 DTO 리스트 반환
    default List<ObjectResponse> toObjectResponseList(List<ObjectResponse> dtos, Map<String, Boolean> likeMap) {
        return dtos.stream()
                .map(dto -> {
                    Boolean liked = likeMap.getOrDefault(dto.objectId(), false);
                    return new ObjectResponse(
                            dto.objectId(),
                            dto.objectName(),
                            dto.intro(),
                            dto.imageUrl(),
                            dto.objectPrice(),
                            dto.soldOut(),
                            liked
                    );
                }).collect(Collectors.toList());
    }

    default BoardResponse toBoardResponse(List<ObjectResponse> dtos, Map<String, Boolean> likeMap, boolean hasNext) {
        List<ObjectResponse> resultObjects = toObjectResponseList(dtos, likeMap);
        return new BoardResponse(resultObjects.size(), resultObjects, hasNext);
    }
}