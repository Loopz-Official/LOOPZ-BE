package kr.co.loopz.dto.response;


public record ObjectResponse (
        String objectId,
        String objectName,
        String intro,
        String imageUrl,
        int objectPrice,
        boolean soldOut,
        Boolean liked
){
}


