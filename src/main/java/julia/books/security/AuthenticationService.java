package julia.books.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager,
                                 TokenService tokenService,
                                 UserDetailsServiceImpl userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    public Token createAuthenticationToken(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        var userDetails = userDetailsService.loadUserByUsername(username);
        return tokenService.generateToken(userDetails);
    }

    public Token refreshToken(String refreshSession) {
        return tokenService.refreshToken(refreshSession);
    }

    public void deleteToken(String token) {
        tokenService.deleteToken(token);
    }
}
