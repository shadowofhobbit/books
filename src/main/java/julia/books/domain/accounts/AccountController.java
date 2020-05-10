package julia.books.domain.accounts;

import julia.books.security.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    Token registerUser(@RequestBody RegistrationInvoice registrationInvoice) {
        return accountService.registerUser(registrationInvoice);
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(value = "hasAuthority('ADMIN')")
    Account register(@RequestBody RegistrationInvoice registrationInvoice) {
        return accountService.register(registrationInvoice);
    }

}
