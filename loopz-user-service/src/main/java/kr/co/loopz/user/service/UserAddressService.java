package kr.co.loopz.user.service;

import kr.co.loopz.user.converter.UserConverter;
import kr.co.loopz.user.domain.Address;
import kr.co.loopz.user.dto.request.AddressRegisterRequest;
import kr.co.loopz.user.dto.response.AddressListResponse;
import kr.co.loopz.user.dto.response.AddressResponse;
import kr.co.loopz.user.exception.UserException;
import kr.co.loopz.user.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.co.loopz.user.exception.UserErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserAddressService {

    private final UserConverter userConverter;
    private final AddressRepository addressRepository;

    //배송지 등록
    @Transactional
    public AddressResponse registerAddress(String userId, AddressRegisterRequest request) {

        // 사용자가 등록한 기존 배송지 개수를 조회
        boolean isFirstAddress = addressRepository.countByUserId(userId) == 0;

        // 첫 배송지인데 request.isDefault()가 false면 예외
        if (isFirstAddress && !request.isDefault()) {
            throw new UserException(FIRST_ADDRESS_MUST_BE_DEFAULT);
        }

        // 이미 동일한 주소가 존재하면 예외
        boolean exists = addressRepository.existsByUserIdAndZoneCodeAndAddressAndAddressDetail(
                userId,
                request.zoneCode(),
                request.address(),
                request.addressDetail()
        );

        if (exists) {
            throw new UserException(ADDRESS_EXISTS);
        }

        // 첫 배송지이거나 요청에서 기본 배송지로 설정했으면 true
        boolean isDefault = isFirstAddress || request.isDefault();

        // 기본배송지 중복체크
        if (isDefault) {
            boolean defaultAddressExists = addressRepository.existsByUserIdAndIsDefaultTrue(userId);
            if (!isFirstAddress && defaultAddressExists) {
                throw new UserException(ALREADY_HAS_DEFAULT_ADDRESS);
            }
        }

        // Address 엔티티 생성
        Address address = Address.from(request, userId, isDefault);

        Address saved = addressRepository.save(address);

        return userConverter.toAddressResponse(saved);
    }

    //배송지 목록 조회
    public AddressListResponse getAddressList(String userId) {
        List<Address> addresses = addressRepository.findAllByUserId(userId);
        List<AddressResponse> addressResponses = addresses.stream()
                .map(userConverter::toAddressResponse)
                .toList();

        return new AddressListResponse(addressResponses);
    }


}
