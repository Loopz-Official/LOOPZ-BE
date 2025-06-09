package kr.co.loopz.user.apiExternal;

import jakarta.validation.Valid;
import kr.co.loopz.user.dto.request.AgreeTermsRequest;
import kr.co.loopz.user.dto.response.AgreeTermsResponse;
import kr.co.loopz.user.service.UserTermsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/v1")
@Slf4j
public class UserTermsController {

    private final UserTermsService userTermsService;

    @PatchMapping("/terms")
    public ResponseEntity<AgreeTermsResponse> agreeTerms(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody AgreeTermsRequest agreeTermsRequest
            ) {

        String userId = currentUser.getUsername();
        log.debug("Agreeing to terms for user: {}", userId);

        AgreeTermsResponse response = userTermsService.agreeTerms(userId, agreeTermsRequest);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
