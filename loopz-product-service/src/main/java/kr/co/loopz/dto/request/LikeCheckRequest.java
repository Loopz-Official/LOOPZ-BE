package kr.co.loopz.dto.request;

import java.util.List;

public record LikeCheckRequest (
        List<String> objectIds
){
}
