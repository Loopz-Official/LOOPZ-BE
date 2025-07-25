package kr.co.loopz.search.dto.response;

public record ObjectResponse(

        String objectId,
        String objectName,
        String intro,
        String imageUrl,
        Long objectPrice,
        boolean soldOut,
        Boolean liked,
        int stock
){
}
