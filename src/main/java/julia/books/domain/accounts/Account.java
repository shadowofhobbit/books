package julia.books.domain.accounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Account {
    private Integer id;
    private String username;
    private String email;
    private boolean confirmedEmail;
    private LocalDate birthday;
    private AccountRole role;
    private String description;
}
