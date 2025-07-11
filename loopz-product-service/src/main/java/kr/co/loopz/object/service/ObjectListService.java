package kr.co.loopz.object.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.loopz.object.converter.ObjectConverter;
import kr.co.loopz.object.domain.ObjectEntity;
import kr.co.loopz.object.domain.QObjectEntity;
import kr.co.loopz.object.dto.request.FilterRequest;
import kr.co.loopz.object.dto.response.BoardResponse;
import kr.co.loopz.object.dto.response.ObjectResponse;
import kr.co.loopz.object.repository.LikeRepository;
import kr.co.loopz.object.repository.ObjectImageRepository;
import kr.co.loopz.object.repository.ObjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ObjectListService extends AbstractObjectService{

    public ObjectListService(
            ObjectImageRepository objectImageRepository,
            LikeRepository likeRepository,
            ObjectRepository objectRepository,
            ObjectConverter objectConverter,
            JPAQueryFactory queryFactory
    ) {
        super(objectImageRepository, likeRepository, objectRepository, objectConverter, queryFactory);
    }

    // 오브제 보드 조회
    public BoardResponse getBoard(String userId, FilterRequest filter) {
        Slice<ObjectEntity> slice = getFilteredObjects(filter);
        List<ObjectEntity> entities = slice.getContent();

        List<String> objectIds = entities.stream()
                .map(ObjectEntity::getObjectId)
                .collect(Collectors.toList());
        List<ObjectResponse> objectResponses = objectConverter.toObjectResponseList(entities);

        Map<String, String> imageMap = loadThumbnails(objectIds);
        Map<String, Boolean> likeMap = loadLikeMap(userId, objectIds);

        long filteredCount = countFilteredObjects(buildFilter(filter, QObjectEntity.objectEntity));
        return objectConverter.toBoardResponse((int) filteredCount, objectResponses, imageMap, likeMap, slice.hasNext());
    }


    public Slice<ObjectEntity> getFilteredObjects(FilterRequest filter) {
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
        BooleanBuilder builder = buildFilter(filter, QObjectEntity.objectEntity);
        boolean[] hasNextHolder = new boolean[1];

        List<ObjectEntity> result = fetchFilteredObjects(builder, pageable, filter.getSort(), filter.getSize(), hasNextHolder);
        return new SliceImpl<>(result, pageable, hasNextHolder[0]);
    }


    // 필터
    private BooleanBuilder buildFilter(FilterRequest filter, QObjectEntity qObj) {
        BooleanBuilder builder = new BooleanBuilder();

        if (filter.getObjectTypes() != null && !filter.getObjectTypes().isEmpty()) {
            builder.and(qObj.objectType.in(filter.getObjectTypes()));
        }
        if (filter.getObjectSizes() != null && !filter.getObjectSizes().isEmpty()) {
            builder.and(qObj.objectSize.in(filter.getObjectSizes()));
        }
        if (filter.getPriceMin() != null) {
            builder.and(qObj.objectPrice.goe(filter.getPriceMin()));
        }
        if (filter.getPriceMax() != null) {
            builder.and(qObj.objectPrice.loe(filter.getPriceMax()));
        }
        if (filter.getKeywords() != null && !filter.getKeywords().isEmpty()) {
            builder.and(qObj.keywords.any().in(filter.getKeywords()));
        }
        if (Boolean.TRUE.equals(filter.getExcludeSoldOut())) {
            builder.and(qObj.soldOut.isFalse());
        }
        return builder;
    }


}
