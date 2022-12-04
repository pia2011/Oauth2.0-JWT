package oauth2jwt.demo.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import oauth2jwt.demo.auth.exception.TokenValidFailedException;
import oauth2jwt.demo.user.enumerate.RoleType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AuthTokenProvider {

    private final Key key;
    private static final String AUTHORITIES_KEY = "role";

    @Value("${app.auth.accessTokenExpiry}")
    private String accessTokenExpiry;

    @Value("${app.auth.refreshTokenExpiry}")
    private String refreshTokenExpiry;

    public AuthTokenProvider(@Value("${app.auth.tokenSecret}") String secretKey){
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public AuthToken createToken(String id, RoleType roleType, String expiry, TokenType tokenType){
        Date expireDate = getExpireDate(expiry);
        return new AuthToken(id, roleType, expireDate, key, tokenType);
    }

    public AuthToken createAccessToken(String id, RoleType roleType){
        return createToken(id, roleType, accessTokenExpiry, TokenType.ATK);
    }
    public AuthToken createRefreshToken(String id, RoleType roleType){
        return createToken(id, roleType, refreshTokenExpiry, TokenType.RTK);
    }

    public AuthToken convertToken(String token){
        return new AuthToken(token, key);
    }

    private Date getExpireDate(String expiry) {

        return new Date(System.currentTimeMillis() + Long.parseLong(expiry));
    }

    public long getRefreshExpireToSec(){
        return Long.parseLong(refreshTokenExpiry)/1000;
    }

    public Authentication getAuthentication(AuthToken authToken){

        if(authToken.validate()){
            Claims claims = authToken.getTokenClaim();
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(new String[]{claims.get(AUTHORITIES_KEY).toString()})
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            User principal = new User(claims.getSubject(), "", authorities);
            return new UsernamePasswordAuthenticationToken(principal, authToken, authorities);
        }else {
            throw new TokenValidFailedException();
        }
    }


}
