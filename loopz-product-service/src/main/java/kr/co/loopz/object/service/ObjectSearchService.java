package kr.co.loopz.object.service;

import com.querydsl.core.BooleanBuilder;
import kr.co.loopz.object.converter.ObjectConverter;
import kr.co.loopz.object.domain.ObjectEntity;
import kr.co.loopz.object.domain.QObjectEntity;
import kr.co.loopz.object.dto.request.SearchFilterRequest;
import kr.co.loopz.object.dto.response.BoardResponse;
import kr.co.loopz.object.dto.response.ObjectNameResponse;
import kr.co.loopz.object.repository.ObjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ObjectSearchService{

    private final ObjectBoardService objectBoardService;
    private final ObjectRepository objectRepository;

    private final ObjectConverter objectConverter;


    public BoardResponse findObjectBySearchFilter(String userId, SearchFilterRequest filter) {
        BooleanBuilder whereClause = buildWhereClause(filter);
        return objectBoardService.getBoardResponse(userId, filter, whereClause);
    }

    // 실시간 검색: objectName 기준 Top 10
    public List<ObjectNameResponse> findObjectBySearchFilter(String keyword) {
        List<ObjectEntity> result = objectRepository.findTop10ByObjectNameContainingIgnoreCase(keyword);
        return objectConverter.toObjectNameResponseList(result);
    }

    private BooleanBuilder buildWhereClause(SearchFilterRequest filter) {

        QObjectEntity object = QObjectEntity.objectEntity;
        BooleanBuilder builder = new BooleanBuilder();

        if (filter.searchWord() != null && !filter.searchWord().isEmpty()) {
            String pattern = "%" + filter.searchWord() + "%";
            builder.and(object.objectName.likeIgnoreCase(pattern)
                    .or(object.intro.likeIgnoreCase(pattern)));
        }
        if (Boolean.TRUE.equals(filter.excludeSoldOut())) {
            builder.and(object.soldOut.eq(false));
        }

        return builder;
    }

}
