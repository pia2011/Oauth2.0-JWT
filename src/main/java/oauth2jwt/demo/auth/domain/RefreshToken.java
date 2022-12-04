package oauth2jwt.demo.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Builder
@RedisHash(value = "Token")
public class RefreshToken {

    @Id
    private String id;

    @Indexed
    private String token;

    @TimeToLive
    private Long expiry;
}
