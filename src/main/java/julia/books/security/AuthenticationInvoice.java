package julia.books.security;

import lombok.Data;

@Data
class AuthenticationInvoice {
    private String username;
    private String password;
}
