package kr.co.loopz.object.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.loopz.object.converter.ObjectConverter;
import kr.co.loopz.object.domain.*;
import kr.co.loopz.object.dto.request.SearchFilterRequest;
import kr.co.loopz.object.dto.response.BoardResponse;
import kr.co.loopz.object.dto.response.ObjectNameResponse;
import kr.co.loopz.object.dto.response.ObjectResponse;
import kr.co.loopz.object.repository.LikeRepository;
import kr.co.loopz.object.repository.ObjectImageRepository;
import kr.co.loopz.object.repository.ObjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Slf4j
@Service
public class ObjectSearchService extends AbstractObjectService{

    public ObjectSearchService(
            ObjectImageRepository objectImageRepository,
            LikeRepository likeRepository,
            ObjectRepository objectRepository,
            ObjectConverter objectConverter,
            JPAQueryFactory queryFactory
    ) {
        super(objectImageRepository, likeRepository, objectRepository, objectConverter, queryFactory);
    }


    @Transactional
    public BoardResponse searchObjectsByKeyword(String userId, SearchFilterRequest filter) {
        Pageable pageable = PageRequest.of(filter.page(), filter.size());
        BooleanBuilder builder = buildKeywordFilter(filter);

        boolean[] hasNextHolder = new boolean[1];
        List<ObjectEntity> objects = fetchFilteredObjects(builder, pageable, filter.sort(), filter.size(), hasNextHolder);

        return buildBoardResponse(userId, objects, hasNextHolder[0], pageable);
    }

    // 검색 후 보드 반환
    private BoardResponse buildBoardResponse(String userId, List<ObjectEntity> entities, boolean hasNext, Pageable pageable) {
        List<String> objectIds = entities.stream()
                .map(ObjectEntity::getObjectId)
                .collect(Collectors.toList());

        List<ObjectResponse> objects = objectConverter.toObjectResponseList(entities);
        Map<String, String> imageMap = loadThumbnails(objectIds);
        Map<String, Boolean> likeMap = loadLikeMap(userId, objectIds);

        long totalCount = objectRepository.count();
        return objectConverter.toBoardResponse((int) totalCount, objects, imageMap, likeMap, hasNext);
    }

    // 실시간 검색: objectName 기준 Top 10
    public List<ObjectNameResponse> searchObjectsByKeyword(String keyword) {
        List<ObjectEntity> result = objectRepository.findTop10ByObjectNameContainingIgnoreCase(keyword);
        return objectConverter.toObjectNameResponseList(result);
    }

    // 키워드 입력
    private BooleanBuilder buildKeywordFilter(SearchFilterRequest filter) {
        QObjectEntity object = QObjectEntity.objectEntity;
        BooleanBuilder builder = new BooleanBuilder();

        if (filter.keyword() != null && !filter.keyword().isEmpty()) {
            String pattern = "%" + filter.keyword() + "%";
            builder.and(object.objectName.likeIgnoreCase(pattern)
                    .or(object.intro.likeIgnoreCase(pattern)));
        }
        if (Boolean.TRUE.equals(filter.excludeSoldOut())) {
            builder.and(object.soldOut.eq(false));
        }

        return builder;
    }

}
