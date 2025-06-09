package kr.co.loopz.user.converter;

import kr.co.loopz.user.domain.UserEntity;
import kr.co.loopz.user.dto.response.AgreeTermsResponse;
import kr.co.loopz.user.dto.response.NickNameUpdateResponse;
import kr.co.loopz.user.dto.response.UserInternalRegisterResponse;
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
    NickNameUpdateResponse toNickNameUpdateResponse(UserEntity user);

    @Mapping(source = "userTerms.over14", target = "over14")
    @Mapping(source = "userTerms.agreedTerms", target = "agreedTerms")
    @Mapping(source = "userTerms.agreedPrivacyPolicy", target = "agreedPrivacyPolicy")
    @Mapping(source = "userTerms.agreedSMSMarketing", target = "agreedSMSMarketing")
    AgreeTermsResponse toAgreeTermsResponse(UserEntity user);

}
