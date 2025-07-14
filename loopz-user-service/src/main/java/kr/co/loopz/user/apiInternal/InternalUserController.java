package kr.co.loopz.user.apiInternal;

import kr.co.loopz.user.converter.UserConverter;
import kr.co.loopz.user.domain.Address;
import kr.co.loopz.user.dto.request.UserInternalRegisterRequest;
import kr.co.loopz.user.dto.response.AddressResponse;
import kr.co.loopz.user.dto.response.UserInternalRegisterResponse;
import kr.co.loopz.user.repository.AddressRepository;
import kr.co.loopz.user.repository.UserRepository;
import kr.co.loopz.user.service.UserAddressService;
import kr.co.loopz.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/user")
@Slf4j
public class InternalUserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final UserAddressService userAddressService;

    @PostMapping
    public UserInternalRegisterResponse getOrCreateUser(
            @RequestBody UserInternalRegisterRequest registerRequest) {

        log.debug("Received request to get or create user: {}", registerRequest);
        return userService.getOrCreateUser(registerRequest);
    }

    @GetMapping("/{userId}/exists")
    public boolean existsByUserId(@PathVariable("userId") String userId) {
        log.debug("Checking existence for userId: {}", userId);
        return userRepository.existsByUserId(userId);
    }

    @GetMapping("/{userId}/addresses/{addressId}/exists")
    public boolean existsAddressByUserId(
            @PathVariable("userId") String userId,
            @PathVariable("addressId") String addressId) {

        log.debug("Checking existence for addressId: {} of userId: {}", addressId, userId);
        return addressRepository.existsByUserIdAndAddressId(userId, addressId);
    }

    @GetMapping("/{userId}/addresses/{addressId}")
    public AddressResponse getAddressById(@PathVariable String userId,@PathVariable String addressId) {
        Address address = userAddressService.getAddress(userId, addressId);
        return UserConverter.INSTANCE.toAddressResponse(address);
    }
}
