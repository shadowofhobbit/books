package julia.books.domain.accounts;

import julia.books.domain.reviews.ReviewEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

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
    @OneToMany(mappedBy = "reviewer")
    private Set<ReviewEntity> reviews;
}
