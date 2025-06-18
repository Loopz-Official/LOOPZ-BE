package kr.co.loopz.authentication.converter;

import kr.co.loopz.authentication.constants.SocialLoginType;
import kr.co.loopz.authentication.dto.request.InternalRegisterRequest;
import kr.co.loopz.authentication.dto.response.GoogleResourceServerResponse;
import kr.co.loopz.authentication.dto.response.InternalRegisterResponse;
import kr.co.loopz.authentication.dto.response.KakaoResourceServerResponse;
import kr.co.loopz.authentication.dto.response.SocialLoginResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(
        componentModel = SPRING,
        unmappedTargetPolicy = IGNORE
)
public interface AuthConverter {

    AuthConverter INSTANCE = Mappers.getMapper(AuthConverter.class);

    @Mapping(target = "socialLoginType", constant = "GOOGLE")
    InternalRegisterRequest toInternalRegisterRequest(GoogleResourceServerResponse response);

    @Mapping(target = "email", source = "kakaoAccount.email")
    @Mapping(target = "name", source = "properties.nickname")
    @Mapping(target = "givenName", source = "properties.nickname")
    @Mapping(target = "familyName", expression = "java(\"\")")
    @Mapping(target = "picture", source = "properties.profileImage")
    @Mapping(target = "socialLoginType", constant = "KAKAO")
    InternalRegisterRequest toInternalRegisterRequest(KakaoResourceServerResponse response);

    SocialLoginResponse toSocialLoginResponse(InternalRegisterResponse response);

    default MultiValueMap<String, Object> toKakaoTokenRequest(String accessCode, String kakaoClientId, String kakaoRedirectUri) {
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", kakaoClientId);
        formData.add("redirect_uri", kakaoRedirectUri);
        formData.add("code", accessCode);
        formData.add("client_secret", "");
        return formData;
    }


}
