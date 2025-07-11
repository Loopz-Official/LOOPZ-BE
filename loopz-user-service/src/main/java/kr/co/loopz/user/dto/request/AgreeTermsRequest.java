package kr.co.loopz.user.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record AgreeTermsRequest(
    @NotNull
    @AssertTrue
    Boolean over14,
    @NotNull
    @AssertTrue
    Boolean agreedServiceTerms,
    @NotNull
    Boolean agreedMarketing,
    @NotNull
    Boolean agreedEventSMS
) {
}
