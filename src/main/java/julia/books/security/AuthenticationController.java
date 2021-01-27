package julia.books.security;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import java.security.Principal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/auth")
@Api(tags = "Authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Log in with username and password. Returns access and refresh tokens")
    public ResponseEntity<Token> createAuthenticationToken(@RequestBody @ApiParam("Credentials") AuthenticationDTO authenticationRequest) {
        final var token = authenticationService.createAuthenticationToken(authenticationRequest.getUsername(),
                authenticationRequest.getPassword());
        return createResponseWithCookie(token);
    }

    @PostMapping("/refresh")
    @ApiOperation("Refresh token")
    public ResponseEntity<Token> refresh(@CookieValue("refreshToken") Cookie refreshCookie) {
        final Token token = authenticationService.refreshToken(refreshCookie.getValue());
        return createResponseWithCookie(token);

    }

    private ResponseEntity<Token> createResponseWithCookie(Token token) {
        final var cookie = ResponseCookie.from("refreshToken",
                token.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/auth")
                .maxAge(Duration.of(60, ChronoUnit.DAYS))
                .build();
        final var headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return new ResponseEntity<>(token, headers, HttpStatus.OK);
    }

    @GetMapping("/authenticated")
    @ApiOperation("Check if user is logged in")
    public Boolean validate(Principal principal) {
        return principal != null && ((Authentication) principal).isAuthenticated();
    }

    @DeleteMapping("/logout")
    @ApiOperation("Log out")
    public void logout(@CookieValue("refreshToken") Cookie refreshCookie) {
        authenticationService.deleteToken(refreshCookie.getValue());
    }
}
