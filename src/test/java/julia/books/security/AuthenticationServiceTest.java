package julia.books.security;

import julia.books.domain.accounts.AccountRepository;
import julia.books.domain.accounts.AccountRole;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
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

    @Before
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

    @Test(expected = UsernameNotFoundException.class)
    public void createAuthenticationTokenUserNotFound() {
        when(userDetailsService.loadUserByUsername("test")).thenThrow(UsernameNotFoundException.class);
        authenticationService.createAuthenticationToken("test", "test");
        verifyNoMoreInteractions(tokenService);
    }

    @Test(expected = BadCredentialsException.class)
    public void createAuthenticationTokenWrongPassword() {
        when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);
        authenticationService.createAuthenticationToken("test", "test");
        verifyNoMoreInteractions(tokenService);
    }
}
