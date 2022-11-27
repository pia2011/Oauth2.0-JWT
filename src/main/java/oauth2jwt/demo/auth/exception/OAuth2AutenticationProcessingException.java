package oauth2jwt.demo.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class OAuth2AutenticationProcessingException extends AuthenticationException {
    
    public OAuth2AutenticationProcessingException(String msg) {
        super(msg);
    }
}
