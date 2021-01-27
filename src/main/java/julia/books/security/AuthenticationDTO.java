package julia.books.security;

import lombok.Data;

@Data
class AuthenticationDTO {
    private String username;
    private String password;
}
