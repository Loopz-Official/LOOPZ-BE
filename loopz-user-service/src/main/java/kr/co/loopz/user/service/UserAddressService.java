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

        // 이미 동일한 주소가 존재하면 예외
        checkDuplicatedAddress(userId, request.zoneCode(), request.address(), request.addressDetail());

        // 기본 주소 설정 처리
        if (request.defaultAddress()) {
            unsetDefaultAddress(userId);
        }

        Address address = Address.from(request, userId, request.defaultAddress());
        addressRepository.save(address);
        return userConverter.toAddressResponse(address);
    }

    // 배송지 목록 조회
    public AddressListResponse getAddressList(String userId) {
        List<Address> addresses = addressRepository.findAllByUserIdOrderByIdAsc(userId);
        List<AddressResponse> addressResponses = addresses.stream()
                .map(userConverter::toAddressResponse)
                .toList();

        return new AddressListResponse(addressResponses);
    }

    public AddressResponse getAddressResponse(String userId, String addressId) {
        Address address = getAddress(userId, addressId);
        return userConverter.toAddressResponse(address);
    }

    // 배송지 수정
    @Transactional
    public AddressResponse updateAddress(String userId, String addressId, AddressUpdateRequest request) {
        Address existingAddress = getAddress(userId, addressId);
        boolean onlyDefault = isOnlyDefaultChanged(existingAddress, request);

        // 실제 반영될 값으로 중복 체크
        if (!onlyDefault) {
            checkDuplicatedAddressWhenPatch(userId, request, existingAddress);
        }

        existingAddress.update(request);
        existingAddress.updateDefaultAddress(request.defaultAddress(), () -> unsetDefaultAddress(userId));
        return userConverter.toAddressResponse(existingAddress);
    }

    // 배송지 삭제
    @Transactional
    public void deleteAddress(String userId, String addressId){
        Address address = getAddress(userId, addressId);
        addressRepository.delete(address);
    }



    // 기존 기본 배송지 해제
    private void unsetDefaultAddress(String userId) {
        addressRepository.findByUserIdAndDefaultAddressTrue(userId)
                .ifPresent(existingDefault -> {
                    existingDefault.clearDefault();
                    addressRepository.save(existingDefault);
                });
    }

    // default 외 필드 변경 여부 체크
    private boolean isOnlyDefaultChanged(Address address, AddressUpdateRequest request) {
        if (request.address() != null && !request.address().equals(address.getAddress())) return false;
        if (request.addressDetail() != null && !request.addressDetail().equals(address.getAddressDetail())) return false;
        if (request.zoneCode() != null && !request.zoneCode().equals(address.getZoneCode())) return false;
        if (request.recipientName()!= null && !request.recipientName().equals(address.getRecipientName())) return false;
        if (request.phoneNumber() != null && !request.phoneNumber().equals(address.getPhoneNumber())) return false;
        return true;
    }


    private void checkDuplicatedAddress(String userId, String zoneCode, String address, String detail) {
        if (addressRepository.existsByUserIdAndZoneCodeAndAddressAndAddressDetail(
                userId, zoneCode, address, detail)) {
            throw new UserException(ADDRESS_EXISTS);
        }
    }

    private void checkDuplicatedAddressWhenPatch(String userId, AddressUpdateRequest req, Address existing) {
        String zone = req.zoneCode() != null ? req.zoneCode() : existing.getZoneCode();
        String address = req.address() != null ? req.address() : existing.getAddress();
        String detail = req.addressDetail() != null ? req.addressDetail() : existing.getAddressDetail();

        if (addressRepository.existsByUserIdAndZoneCodeAndAddressAndAddressDetailAndIdNot(
                userId, zone, address, detail, existing.getId())) {
            throw new UserException(ADDRESS_EXISTS);
        }
    }

    public Address getAddress(String userId, String addressId) {
        return addressRepository.findByAddressIdAndUserId(addressId, userId)
                .orElseThrow(() -> new UserException(ADDRESS_NOT_FOUND, "Address id: " + addressId));
    }

}
