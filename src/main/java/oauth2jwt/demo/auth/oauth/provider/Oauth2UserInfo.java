package oauth2jwt.demo.auth.oauth.provider;

import oauth2jwt.demo.user.enumerate.Provider;

import java.util.Map;

public abstract class Oauth2UserInfo {
    protected Map<String, Object> attributes;
    public Oauth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    public abstract String getEmail();
    public abstract String getName();
    public abstract Provider getProvider();
    public abstract String getProviderId();
}
