package julia.books.security;

import lombok.Data;

@Data
public class Token {
    private final String accessToken;
    private final String refreshToken;
}
