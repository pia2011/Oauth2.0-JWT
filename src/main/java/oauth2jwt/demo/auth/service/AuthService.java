package oauth2jwt.demo.auth.service;

import lombok.RequiredArgsConstructor;
import oauth2jwt.demo.auth.domain.RefreshToken;
import oauth2jwt.demo.auth.dto.AuthResponse;
import oauth2jwt.demo.auth.jwt.AuthToken;
import oauth2jwt.demo.auth.jwt.AuthTokenProvider;
import oauth2jwt.demo.auth.repository.RefreshTokenRedisRepository;
import oauth2jwt.demo.user.enumerate.RoleType;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RefreshTokenRedisRepository redisRepository;
    private final AuthTokenProvider authTokenProvider;

    @Transactional
    public AuthResponse findAndCreateToken(String token) {

        Optional<RefreshToken> optionalRefreshToken = redisRepository.findByToken(token);
        /**
         * 토큰이 있으면 ATK 생성 후 반환
         * 토큰이 없으면(기간이 만료된 것이므로) null 반환
         */
        if (optionalRefreshToken.isPresent()) {
            RefreshToken refreshToken = optionalRefreshToken.get();

            AuthToken refreshAuthToken = authTokenProvider.convertToken(refreshToken.getToken());
            /**
             * TODO : 문제점 고치기
             * 1. 로직 개선
             * 2. RoleType
             */
            String role = refreshAuthToken.getTokenClaim().get("role").toString();

            RoleType roleType = role.equals("ROLE_USER") ? RoleType.ROLE_USER : RoleType.ROLE_ADMIN;

            AuthToken accessToken = authTokenProvider.createAccessToken(refreshToken.getId(), roleType);
            return AuthResponse.builder().accessToken(accessToken.getToken()).build();
        }
        return null;
    }


    @Transactional
    public AuthResponse createAndSaveToken(String id, RoleType roleType){

        AuthToken accessToken = authTokenProvider.createAccessToken(id, roleType);
        AuthToken refreshToken = authTokenProvider.createRefreshToken(id, roleType);

        RefreshToken rToken = RefreshToken.builder()
                .id(id)
                .token(refreshToken.getToken())
                .expiry(authTokenProvider.getRefreshExpireToSec())
                .build();

        redisRepository.save(rToken);

        return AuthResponse.builder()
                .accessToken(accessToken.getToken())
                .refreshToken(refreshToken.getToken())
                .build();
    }


}
