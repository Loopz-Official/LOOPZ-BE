package kr.co.loopz.user.repository;

import kr.co.loopz.user.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    // userId로 주소 개수 조회
    long countByUserId(String userId);

    boolean existsByUserIdAndZoneCodeAndAddressAndAddressDetail(
            String userId, String zoneCode, String address, String addressDetail);

    boolean existsByUserIdAndIsDefaultTrue(String userId);

    List<Address> findAllByUserId(String userId);
}
