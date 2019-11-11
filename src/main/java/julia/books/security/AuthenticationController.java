package julia.books.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "/authenticate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Token createAuthenticationToken(@RequestBody AuthenticationInvoice authenticationRequest) {
        return authenticationService.createAuthenticationToken(authenticationRequest.getUsername(),
                authenticationRequest.getPassword());
    }

    @GetMapping("/authenticated")
    public Boolean validate() {
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }

}
