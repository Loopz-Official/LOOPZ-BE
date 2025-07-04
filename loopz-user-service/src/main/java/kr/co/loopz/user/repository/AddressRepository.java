package kr.co.loopz.user.repository;

import kr.co.loopz.user.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    // userId로 주소 개수 조회
    long countByUserId(String userId);

    boolean existsByUserIdAndZoneCodeAndAddressAndAddressDetailAndDefaultAddress(
            String userId, String zoneCode, String address, String addressDetail, boolean defaultAddress);

    Optional<Address> findByUserIdAndDefaultAddressTrue(String userId);

    Optional<Address> findByAddressIdAndUserId(String addressId, String userId);

    List<Address> findAllByUserIdOrderByIdAsc(String userId);

    boolean existsByUserIdAndAddressId(String userId, String addressId);
}

