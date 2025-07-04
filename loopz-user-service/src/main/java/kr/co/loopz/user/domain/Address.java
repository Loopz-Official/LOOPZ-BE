package kr.co.loopz.user.domain;

import jakarta.persistence.*;
import kr.co.loopz.common.domain.BaseTimeEntity;
import kr.co.loopz.common.domain.BaseTimeEntityWithDeletion;
import kr.co.loopz.user.dto.request.AddressRegisterRequest;
import kr.co.loopz.user.dto.request.AddressUpdateRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
public class Address extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String addressId;

    private String userId;

    //수령자
    private String recipientName;

    private String phoneNumber;

    //우편번호
    private String zoneCode;

    //주소
    private String address;

    //상세주소
    private String addressDetail;

    //기본 배송지 여부
    private boolean defaultAddress = false;


    @Builder
    private Address(String userId, String recipientName, String phoneNumber,
                    String zoneCode, String address, String addressDetail,
                    boolean defaultAddress) {
        this.addressId = UUID.randomUUID().toString();
        this.userId = userId;
        this.recipientName = recipientName;
        this.phoneNumber = phoneNumber;
        this.zoneCode = zoneCode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.defaultAddress = defaultAddress;
    }

    /**
     * Address 생성 메서드
     */
    public static Address from(AddressRegisterRequest request, String userId, boolean defaultAddress) {
        return Address.builder()
                .userId(userId)
                .recipientName(request.recipientName())
                .phoneNumber(request.phoneNumber())
                .zoneCode(request.zoneCode())
                .address(request.address())
                .addressDetail(request.addressDetail())
                .defaultAddress(defaultAddress)
                .build();
    }

    public void update(AddressUpdateRequest request) {
        if (request.recipientName() != null) {
            this.recipientName = request.recipientName();
        }
        if (request.phoneNumber() != null) {
            this.phoneNumber = request.phoneNumber();
        }
        if (request.zoneCode() != null) {
            this.zoneCode = request.zoneCode();
        }
        if (request.address() != null) {
            this.address = request.address();
        }
        if (request.addressDetail() != null) {
            this.addressDetail = request.addressDetail();
        }
    }

    public void updateDefaultAddress(Boolean requestedDefault, Runnable unsetDefaultAction) {
        if (requestedDefault == null) return;

        if (requestedDefault && !this.defaultAddress) {
            unsetDefaultAction.run();
            this.defaultAddress = true;
        } else if (!requestedDefault) {
            this.defaultAddress = false;
        }
    }

    public void clearDefault() {
        this.defaultAddress = false;
    }
}