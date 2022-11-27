package oauth2jwt.demo.auth.oauth.provider;

import oauth2jwt.demo.user.enumerate.Provider;

import java.util.Map;

public class NaverUserInfo extends Oauth2UserInfo{

    public NaverUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

    @Override
    public Provider getProvider() {
        return Provider.NAVER;
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }
}
