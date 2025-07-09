package kr.co.loopz.search.dto.response;

import java.util.List;

public record BoardResponse(
        List<ObjectResponse> objects,
        boolean hasNext
) {
}
