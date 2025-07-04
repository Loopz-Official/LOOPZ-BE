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
        checkDuplicatedAddress(userId, request.zoneCode(), request.address(), request.addressDetail());

        //요청에서 기본 배송지로 설정했으면 trueg
        boolean isDefault = request.defaultAddress();
        // 기존 기본배송지가 있으면 기본 설정 해제
        if (isDefault) {
            unsetDefaultAddress(userId);
        }

        // Address 엔티티 생성
        Address address = Address.from(request, userId, isDefault);
        addressRepository.save(address);

        return userConverter.toAddressResponse(address);
    }

    //배송지 목록 조회
    public AddressListResponse getAddressList(String userId) {
        List<Address> addresses = addressRepository.findAllByUserIdOrderByIdAsc(userId);
        List<AddressResponse> addressResponses = addresses.stream()
                .map(userConverter::toAddressResponse)
                .toList();

        return new AddressListResponse(addressResponses);
    }

    //배송지 수정
    @Transactional
    public AddressResponse updateAddress(String userId, String addressId, AddressUpdateRequest request) {
        Address address = getAddress(userId, addressId);
        boolean patchOnlyDefault = isOnlyDefaultChanged(address, request);
        if (!patchOnlyDefault) {
            checkDuplicatedAddress(userId, request.zoneCode(), request.address(), request.addressDetail());
        }
        address.update(request);
        address.updateDefaultAddress(request.defaultAddress(), () -> unsetDefaultAddress(userId));

        return userConverter.toAddressResponse(address);
    }

    private boolean isOnlyDefaultChanged(Address address, AddressUpdateRequest req) {
        if (req.address() != null && !req.address().equals(address.getAddress())) {
            return false;
        }
        if (req.addressDetail() != null && !req.addressDetail().equals(address.getAddressDetail())) {
            return false;
        }
        if (req.zoneCode() != null && !req.zoneCode().equals(address.getZoneCode())) {
            return false;
        }
        if (req.recipientName() != null && !req.recipientName().equals(address.getRecipientName())) {
            return false;
        }
        if (req.phoneNumber() != null && !req.phoneNumber().equals(address.getPhoneNumber())) {
            return false;
        }
        return true;
    }

    // 배송지 삭제
    @Transactional
    public void deleteAddress(String userId, String addressId){
        Address address = getAddress(userId, addressId);

        addressRepository.delete(address);
    }

    // 기존 기본배송지 해제
    private void unsetDefaultAddress(String userId) {
        addressRepository.findByUserIdAndDefaultAddressTrue(userId)
                .ifPresent(existingDefault -> {
                    existingDefault.clearDefault();
                    addressRepository.save(existingDefault);
                });
    }

    private void checkDuplicatedAddress(String userId, String zoneCode, String address, String addressDetail) {

        if (addressRepository.existsByUserIdAndZoneCodeAndAddressAndAddressDetail(
                userId,
                zoneCode,
                address,
                addressDetail
        )) {
            throw new UserException(ADDRESS_EXISTS);
        }
    }


    private Address getAddress(String userId, String addressId) {
        return addressRepository.findByAddressIdAndUserId(addressId, userId)
                .orElseThrow(() -> new UserException(ADDRESS_NOT_FOUND,"Address id: "+ addressId));
    }


}
