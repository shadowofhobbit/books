package julia.books.domain.accounts;

import org.springframework.security.core.GrantedAuthority;

public enum AccountRole implements GrantedAuthority {
    USER, ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
