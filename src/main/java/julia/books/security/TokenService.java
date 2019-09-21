package julia.books.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;

@Service
public class TokenService {
    private static final long JWT_TOKEN_VALIDITY_MS = 24 * 60 * 60 * 1000;

    private SecretKey secret = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    String getUsernameFromToken(String token) {
        var claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    Token generateToken(UserDetails userDetails) {
        String compact = Jwts
                .builder()
                .setClaims(new HashMap<>())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY_MS))
                .signWith(secret)
                .compact();
        return new Token(compact);
    }
}
