package kr.co.loopz.user.dto.response;

import java.util.List;

public record AddressListResponse(
        List<AddressResponse> addresses
) {}