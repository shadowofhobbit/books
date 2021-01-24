package julia.books.domain.accounts;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(tags = "Account management")
public class AccountController {

    private final AccountService accountService;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Register")
    public Token registerUser(@RequestBody @Valid @ApiParam("Account data") RegistrationInvoice registrationInvoice) {
        return accountService.registerUser(registrationInvoice);
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Create new account (requires ADMIN role)")
    public Account register(@RequestBody @Valid @ApiParam("Account data") RegistrationInvoice registrationInvoice) {
        return accountService.register(registrationInvoice);
    }

    @GetMapping("/current")
    @ApiOperation("Get current account")
    public Account get(Authentication authentication) {
        final var userDetails = (UserDetailsServiceImpl.CustomUser)authentication.getPrincipal();
        return accountService.get(userDetails.getId());
    }

    @PutMapping("/current")
    @ApiOperation("Update current account")
    public Account update(@RequestBody @Valid @ApiParam("Account data") Account account, Authentication authentication) {
        final var userDetails = (UserDetailsServiceImpl.CustomUser)authentication.getPrincipal();
        return accountService.update(userDetails.getId(), account);
    }

}
