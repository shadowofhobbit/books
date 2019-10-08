package julia.books.domain.accounts;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
class Account {
    private Integer id;
    private String username;
    private String email;
    private String passwordHash;
    private AccountRole role;
}
