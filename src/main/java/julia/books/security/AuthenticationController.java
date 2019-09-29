package julia.books.security;

import julia.books.domain.accounts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserService userDetailsService;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    TokenService tokenService,
                                    UserService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping(value = "/authenticate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Token createAuthenticationToken(@RequestBody AuthenticationInvoice authenticationRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                authenticationRequest.getPassword()));
        var userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        return tokenService.generateToken(userDetails);
    }
}
