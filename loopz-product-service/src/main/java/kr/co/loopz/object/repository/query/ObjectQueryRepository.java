package kr.co.loopz.object.repository.query;

import com.querydsl.core.BooleanBuilder;
import kr.co.loopz.object.domain.ObjectEntity;
import kr.co.loopz.object.dto.request.enums.SortType;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ObjectQueryRepository {
    List<ObjectEntity> findFilteredObjects(BooleanBuilder whereClause, Pageable pageable, SortType sortType, int size);
    long countFilteredObjects(BooleanBuilder whereClause);
    Map<String, String> fetchThumbnails(List<String> objectIds);
    Map<String, Boolean> fetchLikeMap(String userId, List<String> objectIds);
    List<ObjectEntity> findLikedObjects(String userId, Pageable pageable);
    int countLikedObjects(String userId);
}
