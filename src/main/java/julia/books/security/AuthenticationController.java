package julia.books.security;

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
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Token> createAuthenticationToken(@RequestBody AuthenticationInvoice authenticationRequest) {
        var token = authenticationService.createAuthenticationToken(authenticationRequest.getUsername(),
                authenticationRequest.getPassword());
        return setCookieAndReturnTokens(token);
    }

    @PostMapping(value="/refresh")
    public ResponseEntity<Token> refresh(@CookieValue("refreshToken") Cookie refreshCookie) {
        Token token = authenticationService.refreshToken(refreshCookie.getValue());
        return setCookieAndReturnTokens(token);

    }

    private ResponseEntity<Token> setCookieAndReturnTokens(Token token) {
        var cookie = ResponseCookie.from("refreshToken",
                token.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/auth")
                .maxAge(Duration.of(60, ChronoUnit.DAYS))
                .build();
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return new ResponseEntity<>(token, headers, HttpStatus.OK);
    }

    @GetMapping("/authenticated")
    public Boolean validate(Principal principal) {
        return (principal != null) && ((Authentication) principal).isAuthenticated();
    }

    @DeleteMapping("/logout")
    public void logout(@CookieValue("refreshToken") Cookie refreshCookie) {
        authenticationService.deleteToken(refreshCookie.getValue());
    }
}
