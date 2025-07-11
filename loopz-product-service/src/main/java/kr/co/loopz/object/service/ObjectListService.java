package kr.co.loopz.object.service;

import com.querydsl.core.BooleanBuilder;
import kr.co.loopz.object.domain.QObjectEntity;
import kr.co.loopz.object.dto.request.FilterRequest;
import kr.co.loopz.object.dto.response.BoardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObjectListService {

    private final ObjectBoardService objectBoardService;


    public BoardResponse findObjectListByFilter(String userId, FilterRequest filter) {
        BooleanBuilder whereClause = buildWhereClause(filter);
        return objectBoardService.getBoardResponse(userId, filter, whereClause);
    }


    private BooleanBuilder buildWhereClause(FilterRequest filter) {

        BooleanBuilder builder = new BooleanBuilder();
        QObjectEntity object = QObjectEntity.objectEntity;

        if (filter.objectTypes() != null && !filter.objectTypes().isEmpty()) {
            builder.and(object.objectType.in(filter.objectTypes()));
        }
        if (filter.objectSizes() != null && !filter.objectSizes().isEmpty()) {
            builder.and(object.objectSize.in(filter.objectSizes()));
        }
        if (filter.priceMin() != null) {
            builder.and(object.objectPrice.goe(filter.priceMin()));
        }
        if (filter.priceMax() != null) {
            builder.and(object.objectPrice.loe(filter.priceMax()));
        }
        if (filter.keywords() != null && !filter.keywords().isEmpty()) {
            builder.and(object.keywords.any().in(filter.keywords()));
        }
        if (Boolean.TRUE.equals(filter.excludeSoldOut())) {
            builder.and(object.soldOut.isFalse());
        }
        return builder;
    }


}
