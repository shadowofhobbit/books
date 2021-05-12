package julia.books.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import julia.books.domain.accounts.AccountEntity;
import julia.books.domain.accounts.AccountRepository;
import julia.books.error.NoTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
@SuppressWarnings("PMD.DefaultPackage")
@Service
public class TokenService {
    public static final int REFRESH_TOKEN_VALID = 60;

    private final RefreshTokensRepository refreshTokensRepository;
    private final AccountRepository accountRepository;

    private final SecretKey secret = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private static final Duration JWT_DURATION = Duration.of(30, ChronoUnit.MINUTES);

    @Autowired
    public TokenService(RefreshTokensRepository refreshTokensRepository, AccountRepository accountRepository) {
        this.refreshTokensRepository = refreshTokensRepository;
        this.accountRepository = accountRepository;
    }

    String getUsernameFromToken(String token) {
        final var claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    Token generateToken(UserDetailsServiceImpl.CustomUser userDetails) {
        return createToken(userDetails.getUsername(), userDetails.getId());
    }

    @Transactional
    public Token refreshToken(String refreshToken) {
        final var currentToken = refreshTokensRepository.findById(UUID.fromString(refreshToken))
                .orElseThrow(NoTokenException::new);
        refreshTokensRepository.delete(currentToken);
        if (currentToken.getCreatedAt().plus(currentToken.getExpiresIn(), ChronoUnit.DAYS).isBefore(Instant.now())) {
            throw new RuntimeException("Token expired");
        }
        final AccountEntity accountEntity = accountRepository.findById(currentToken.getUserId()).orElseThrow();
        return createToken(accountEntity.getUsername(), currentToken.getUserId());
    }

    private Token createToken(String username, int userId) {
        final var issuedAt = Instant.now();
        final var expiration = issuedAt.plus(JWT_DURATION);
        final String compact = Jwts
                .builder()
                .setClaims(new HashMap<>())
                .setSubject(username)
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(expiration))
                .signWith(secret)
                .compact();
        final UUID uuid = UUID.randomUUID();
        final var refreshToken = new RefreshToken(uuid, userId, issuedAt, REFRESH_TOKEN_VALID);
        refreshTokensRepository.save(refreshToken);
        return new Token(compact, uuid.toString());
    }

    public void deleteToken(String token) {
        refreshTokensRepository.deleteById(UUID.fromString(token));
    }
}
