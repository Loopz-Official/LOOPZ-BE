package kr.co.loopz.user.service;

import kr.co.loopz.user.converter.UserConverter;
import kr.co.loopz.user.domain.Address;
import kr.co.loopz.user.dto.request.AddressRegisterRequest;
import kr.co.loopz.user.dto.request.AddressUpdateRequest;
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

        //요청에서 기본 배송지로 설정했으면 trueg
        boolean isDefault = request.defaultAddress();

        // 기존 기본배송지가 있으면 기본 설정 해제
        if (isDefault) {
            unsetDefaultAddress(userId);
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

    //배송지 수정
    @Transactional
    public AddressResponse updateAddress(String userId, Long addressId, AddressUpdateRequest request) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new UserException(ADDRESS_NOT_FOUND,"Addreess id: "+ addressId));

        if (request.address() != null) {
            address.setAddress(request.address());
        }
        if (request.addressDetail() != null) {
            address.setAddressDetail(request.addressDetail());
        }
        if (request.zoneCode() != null) {
            address.setZoneCode(request.zoneCode());
        }

        if (request.defaultAddress() != null) {
            boolean requestedDefault = request.defaultAddress();
            boolean currentDefault = address.isDefaultAddress();

                if (requestedDefault) {
                    // 기본배송지로 변경 요청 시
                    if (!currentDefault) {
                       unsetDefaultAddress(userId);
                    }
                    address.setDefaultAddress(true);
                } else {
                            address.setDefaultAddress(false);
                        }
                    }

                addressRepository.save(address);


            return userConverter.toAddressResponse(address);
    }

    // 배송지 삭제
    @Transactional
    public void deleteAddress(String userId, Long addressId){
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new UserException(ADDRESS_NOT_FOUND,"Addreess id: "+ addressId));

        addressRepository.delete(address);
    }

    // 기존 기본배송지 해제
    private void unsetDefaultAddress(String userId) {
        addressRepository.findByUserIdAndDefaultAddressTrue(userId)
                .ifPresent(existingDefault -> {
                    existingDefault.setDefaultAddress(false);
                    addressRepository.save(existingDefault);
                });
    }

}
