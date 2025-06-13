package kr.co.loopz.user.apiExternal;

import feign.Response;
import jakarta.validation.Valid;
import kr.co.loopz.user.dto.request.AddressRegisterRequest;
import kr.co.loopz.user.dto.request.NickNameUpdateRequest;
import kr.co.loopz.user.dto.response.AddressRegisterResponse;
import kr.co.loopz.user.dto.response.NickNameUpdateResponse;
import kr.co.loopz.user.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/v1/address")
@RequiredArgsConstructor
@Slf4j
public class UserAddressController {

    private final UserAddressService userAddressService;

    @PostMapping
    public ResponseEntity<AddressRegisterResponse> RegisterAddress(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid AddressRegisterRequest request){

        String userId = currentUser.getUsername();

        AddressRegisterResponse response = userAddressService.registerAddress(userId, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
