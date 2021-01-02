package julia.books.domain.accounts;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

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
    private boolean confirmedEmail;
    private LocalDate birthday;
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    private AccountRole role;
    @Column(columnDefinition = "TEXT")
    private String description;

}
