package oauth2jwt.demo.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oauth2jwt.demo.auth.exception.OAuth2AutenticationProcessingException;
import oauth2jwt.demo.auth.oauth.UserPrincipal;
import oauth2jwt.demo.auth.oauth.provider.GoogleUserInfo;
import oauth2jwt.demo.auth.oauth.provider.KakaoUserInfo;
import oauth2jwt.demo.auth.oauth.provider.NaverUserInfo;
import oauth2jwt.demo.auth.oauth.provider.Oauth2UserInfo;
import oauth2jwt.demo.user.domain.User;
import oauth2jwt.demo.user.enumerate.Provider;
import oauth2jwt.demo.user.enumerate.RoleType;
import oauth2jwt.demo.user.repository.UserRepository;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOauth2UserServcie extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("accessToken : " + userRequest.getAccessToken().getTokenValue());
        log.info("registration : " + userRequest.getClientRegistration());
        log.info("attributes : "+ oAuth2User.getAttributes());
        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex){
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User){
        Oauth2UserInfo oAuth2UserInfo = getUserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User);
        if(oAuth2UserInfo == null){
            throw new OAuth2AutenticationProcessingException("지원하지 않는 로그인 방식을 사용해서 오류 발생");
        }
        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        log.info("registrationId :"+ oAuth2UserRequest.getClientRegistration().getRegistrationId());

        if(userOptional.isPresent()){
            log.info("DB에 있음");
            user = userOptional.get();
            user = updateUser(user, oAuth2UserInfo);
        }else{ // 없음
            log.info("DB에 없음");
            user = registUser(oAuth2UserInfo);
        }

        return new UserPrincipal(user, oAuth2User.getAttributes());
    }

    private User updateUser(User user, Oauth2UserInfo oAuth2UserInfo) {

        user.updateName(oAuth2UserInfo.getName());
        return userRepository.save(user);
    }

    private User registUser(Oauth2UserInfo oauth2UserInfo){

        User user = User.builder()
                .email(oauth2UserInfo.getEmail())
                .name(oauth2UserInfo.getName())
                .roleType(RoleType.ROLE_USER)
                .provider(oauth2UserInfo.getProvider())
                .providerId(oauth2UserInfo.getProviderId())
                .build();

        return userRepository.save(user);
    }

    private Oauth2UserInfo getUserInfo(String registrationId, OAuth2User oauth2User){
        if(registrationId.equals(Provider.GOOGLE.name().toLowerCase())){
            log.info("구글 로그인 요청");
            return new GoogleUserInfo(oauth2User.getAttributes());
        }else if(registrationId.equals(Provider.KAKAO.name().toLowerCase())){
            log.info("카카오 로그인 요청");
            return new KakaoUserInfo(oauth2User.getAttributes());
        }else if(registrationId.equals(Provider.NAVER.name().toLowerCase())){
            log.info("네이버 로그인 요청");
            return new NaverUserInfo((Map)oauth2User.getAttributes().get("response"));
        }else{
            log.info("지원하지 않는 로그인 방식");
            return null;
        }
    }
}
