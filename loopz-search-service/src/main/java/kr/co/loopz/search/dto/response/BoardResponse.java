package kr.co.loopz.search.dto.response;

import java.util.List;

public record BoardResponse(
        int objectCount,
        List<ObjectResponse> objects,
        boolean hasNext
) {
}
