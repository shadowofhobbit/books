package julia.books.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RedisHash("RefreshToken")
@Getter
@AllArgsConstructor
public class RefreshToken {
    @Id
    private final UUID id;
    private final int userId;
    private final Instant createdAt;
    @TimeToLive(unit = TimeUnit.DAYS)
    private final long expiresIn;
}
