package kr.co.loopz.user.dto.request;

import lombok.Getter;

import java.util.List;

public record LikeCheckRequest (
        List<String> objectIds
){
}
