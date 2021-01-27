package julia.books.security;

import julia.books.domain.accounts.AccountRepository;
import julia.books.domain.accounts.AccountRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class AuthenticationServiceTest {
    @MockBean
    @Qualifier("userDetailsServiceImpl")
    UserDetailsServiceImpl userDetailsService;

    @MockBean
    AccountRepository accountRepository;

    @MockBean
    TokenService tokenService;

    @MockBean
    AuthenticationManager authenticationManager;

    private AuthenticationService authenticationService;

    @BeforeEach
    public void setUp() {
        authenticationService = new AuthenticationService(authenticationManager, tokenService, userDetailsService);
        var authInvoice = new AuthenticationInvoice();
        authInvoice.setUsername("test");
        authInvoice.setPassword("test");
        Token token = new Token("header.payload.sig", "gsyauwdgfwoudf");
        when(tokenService.generateToken(any())).thenReturn(token);
    }

    @Test
    public void createAuthenticationTokenUserFound() {
        var userDetails = User.withUsername("test")
                .password(new BCryptPasswordEncoder().encode("test"))
                .authorities(AccountRole.USER)
                .build();
        var user = new UserDetailsServiceImpl.CustomUser(userDetails, 1);
        when(userDetailsService.loadUserByUsername("test")).thenReturn(user);
        authenticationService.createAuthenticationToken("test", "test");
        verify(tokenService).generateToken(user);
    }

    @Test
    public void createAuthenticationTokenUserNotFound() {
        when(userDetailsService.loadUserByUsername("test")).thenThrow(UsernameNotFoundException.class);
        assertThrows(UsernameNotFoundException.class,
                () -> authenticationService.createAuthenticationToken("test", "test"));
        verifyNoMoreInteractions(tokenService);
    }

    @Test
    public void createAuthenticationTokenWrongPassword() {
        when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);
        assertThrows(BadCredentialsException.class,
                () -> authenticationService.createAuthenticationToken("test", "test"));
        verifyNoMoreInteractions(tokenService);
    }
}
