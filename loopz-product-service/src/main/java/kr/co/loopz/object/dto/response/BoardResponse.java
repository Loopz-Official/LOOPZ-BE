package kr.co.loopz.object.dto.response;

import java.util.List;

public record BoardResponse (
        int objectCount,
        List<ObjectResponse> objects,
        boolean hasNext    // Slice 페이징 정보: 다음 페이지 존재 여부
){}
