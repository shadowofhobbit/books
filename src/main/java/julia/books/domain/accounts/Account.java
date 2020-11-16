package julia.books.domain.accounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Account {
    private Integer id;
    private String username;
    private String email;
    private String passwordHash;
    private AccountRole role;
}
