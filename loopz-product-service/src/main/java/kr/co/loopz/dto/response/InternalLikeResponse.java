package kr.co.loopz.dto.response;

import java.util.Map;


public record InternalLikeResponse (
        Map<String, Boolean> likes
){
}
