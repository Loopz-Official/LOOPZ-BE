package kr.co.loopz.object.dto.response;

import java.util.List;

public record DetailResponse(
        ObjectResponse objectResponse,
        String size,
        String descriptionUrl,
        int stock) {}