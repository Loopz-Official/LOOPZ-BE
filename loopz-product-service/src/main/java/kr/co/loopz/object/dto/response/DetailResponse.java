package kr.co.loopz.object.dto.response;

import java.util.List;

public record DetailResponse(
        ObjectResponse objectResponse,
        String shippingInfo,
        int shippingFee,
        String size,
        String descriptionUrl,
        String productInfo,
        String notice,
        int stock,
        List<String> imageUrls) {}