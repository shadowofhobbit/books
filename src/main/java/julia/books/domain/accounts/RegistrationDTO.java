package julia.books.domain.accounts;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.*;

@Data
@Valid
class RegistrationDTO {
    @NotEmpty
    private final String username;
    @NotEmpty
    @Email
    private final String email;
    @NotNull
    @Size(min = 8)
    private final String password;
    @NotNull
    private AccountRole role;
}
