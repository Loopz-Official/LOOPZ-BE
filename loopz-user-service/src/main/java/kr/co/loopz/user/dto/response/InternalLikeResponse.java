package kr.co.loopz.user.dto.response;

import java.util.Map;


public record InternalLikeResponse(
        Map<String, Boolean> likes
){
}
