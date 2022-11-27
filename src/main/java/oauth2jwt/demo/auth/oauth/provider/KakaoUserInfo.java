package oauth2jwt.demo.auth.oauth.provider;

import oauth2jwt.demo.user.enumerate.Provider;

import java.util.Map;

public class KakaoUserInfo extends Oauth2UserInfo{
    private Map<String, Object> attributesAccount;
    private Map<String, Object> attributesProfile;
    public KakaoUserInfo(Map<String, Object> attributes) {
        super(attributes);
        this.attributesAccount = (Map<String, Object>)attributes.get("kakao_account");
        this.attributesProfile = (Map<String, Object>)attributesAccount.get("profile");
    }

    @Override
    public String getEmail() {
        return attributesAccount.get("email").toString();
    }

    @Override
    public String getName() {
        return attributesProfile.get("nickname").toString();
    }

    @Override
    public Provider getProvider() {
        return Provider.KAKAO;
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }
}
