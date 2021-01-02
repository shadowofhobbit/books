package julia.books.domain.accounts;

import julia.books.security.Token;
import julia.books.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    Token registerUser(@RequestBody @Valid RegistrationInvoice registrationInvoice) {
        return accountService.registerUser(registrationInvoice);
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(value = "hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    Account register(@RequestBody @Valid RegistrationInvoice registrationInvoice) {
        return accountService.register(registrationInvoice);
    }

    @GetMapping("/current")
    Account get(Authentication authentication) {
        var userDetails = (UserDetailsServiceImpl.CustomUser)authentication.getPrincipal();
        return accountService.get(userDetails.getId());
    }

    @PutMapping("/current")
    Account update(@RequestBody @Valid Account account, Authentication authentication) {
        System.out.println(account.getBirthday());
        var userDetails = (UserDetailsServiceImpl.CustomUser)authentication.getPrincipal();
        return accountService.update(userDetails.getId(), account);
    }

}
