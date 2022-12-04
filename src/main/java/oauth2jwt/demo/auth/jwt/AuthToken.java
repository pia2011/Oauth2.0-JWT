package oauth2jwt.demo.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecurityException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oauth2jwt.demo.user.enumerate.RoleType;

import java.security.Key;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class AuthToken {

    @Getter
    private final String token;
    private final Key key;

    private static final String AUTHORITIES_KEY = "role";

    public AuthToken(String id, RoleType roleType, Date expiry, Key key, TokenType tokenType) {
        String role = roleType.toString();
        this.key = key;
        this.token = createAuthToken(id, role, expiry, tokenType);
    }

    private String createAuthToken(String id, String role, Date expiry, TokenType tokenType) {
        return Jwts.builder()
                .setSubject(id)
                .claim(AUTHORITIES_KEY,role)
                .claim("type", tokenType)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .compact();
    }

    public boolean validate(){
        return this.getTokenClaim() != null;
    }
    public Claims getTokenClaim(){
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (SecurityException e){
            log.info("Invalid JWT signature");
        }catch (MalformedJwtException e){
            log.info("Invalid JWT token");
        }catch (ExpiredJwtException e){
            log.info("Expired JWT token");
        }catch (UnsupportedJwtException e){
            log.info("Unsupported JWT token");
        }catch (IllegalArgumentException e){
            log.info("invalid");
        }

        return null;
    }
}
