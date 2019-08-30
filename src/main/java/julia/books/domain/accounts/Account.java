package julia.books.domain.accounts;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "accounts")
@Getter
@Setter
class Account {
    @Id
    @GeneratedValue
    private Integer id;
    private String username;
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    private AccountRole role;

}
