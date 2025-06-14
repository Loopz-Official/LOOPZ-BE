package kr.co.loopz.user.apiExternal;

import feign.Response;
import jakarta.validation.Valid;
import kr.co.loopz.user.dto.request.AddressRegisterRequest;
import kr.co.loopz.user.dto.request.NickNameUpdateRequest;
import kr.co.loopz.user.dto.response.AddressListResponse;
import kr.co.loopz.user.dto.response.AddressResponse;
import kr.co.loopz.user.dto.response.NickNameUpdateResponse;
import kr.co.loopz.user.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/v1/address")
@RequiredArgsConstructor
@Slf4j
public class UserAddressController {

    private final UserAddressService userAddressService;

    @PostMapping
    public ResponseEntity<AddressResponse> registerAddress(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid AddressRegisterRequest request){

        String userId = currentUser.getUsername();

        AddressResponse response = userAddressService.registerAddress(userId, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<AddressListResponse> addressList(@AuthenticationPrincipal User currentUser){
        String userId = currentUser.getUsername();

        AddressListResponse response= userAddressService.getAddressList(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
