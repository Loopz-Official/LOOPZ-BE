package kr.co.loopz.user.converter;

import kr.co.loopz.user.domain.Address;
import kr.co.loopz.user.domain.UserEntity;
import kr.co.loopz.user.dto.response.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import static org.mapstruct.ReportingPolicy.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = IGNORE
)
public interface UserConverter {

    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    UserInternalRegisterResponse toUserInternalRegisterResponse(UserEntity userEntity);
    DetailInfoUpdateResponse toDetailInfoUpdateResponse(UserEntity user);

    @Mapping(source = "userTerms.over14", target = "over14")
    @Mapping(source = "userTerms.agreedServiceTerms", target = "agreedServiceTerms")
    @Mapping(source = "userTerms.agreedMarketing", target = "agreedMarketing")
    @Mapping(source = "userTerms.agreedEventSMS", target = "agreedEventSMS")
    AgreeTermsResponse toAgreeTermsResponse(UserEntity user);

    AddressResponse toAddressResponse(Address address);

    UserInfoResponse toUserInfoResponse(UserEntity user);
}
