package julia.books.domain.accounts;

import julia.books.security.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    Token registerUser(@RequestBody RegistrationInvoice registrationInvoice) {
        return accountService.registerUser(registrationInvoice);
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(value = "hasRole('ADMIN')")
    @ResponseStatus(value = HttpStatus.CREATED)
    Account register(@RequestBody RegistrationInvoice registrationInvoice) {
        return accountService.register(registrationInvoice);
    }

}
