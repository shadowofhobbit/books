package julia.books.domain.accounts;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import julia.books.domain.books.SearchResult;
import julia.books.domain.reviews.ReviewDTO;
import julia.books.domain.reviews.ReviewsService;
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
    private final ReviewsService reviewsService;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Register")
    public Token registerUser(@RequestBody @Valid @ApiParam("Account data") RegistrationDTO registrationDTO) {
        return accountService.registerUser(registrationDTO);
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Create new account (requires ADMIN role)")
    public Account register(@RequestBody @Valid @ApiParam("Account data") RegistrationDTO registrationDTO) {
        return accountService.register(registrationDTO);
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

    @GetMapping("/{userId}/reviews")
    @ApiOperation("Get user's reviews")
    public SearchResult<ReviewDTO> getReviewsByUser(@PathVariable Integer userId,
                                                    @RequestParam @ApiParam(value = "Page number", required = true) int page,
                                                    @RequestParam @ApiParam(value = "Page size", required = true) int size) {
        return reviewsService.getReviewsByUserId(userId, page, size);
    }

}
