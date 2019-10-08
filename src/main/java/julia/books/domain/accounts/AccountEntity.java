package julia.books.domain.accounts;

import lombok.*;

import javax.persistence.*;

@Entity(name = "accounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {
    @Id
    @GeneratedValue
    private Integer id;
    private String username;
    private String email;
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    private AccountRole role;

}
