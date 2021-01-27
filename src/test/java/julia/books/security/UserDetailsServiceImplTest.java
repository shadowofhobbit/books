package julia.books.security;

import julia.books.domain.accounts.AccountEntity;
import julia.books.domain.accounts.AccountRepository;
import julia.books.domain.accounts.AccountRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class UserDetailsServiceImplTest {
    @Mock
    private AccountRepository accountRepository;

    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    public void setUp() {
        userDetailsService = new UserDetailsServiceImpl(accountRepository);
    }

    @Test
    public void loadUserByUsernameNoUser() {
        when(accountRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("test"));
    }

    @Test
    public void loadUserByUsernameSuccessfully() {
        var account = new AccountEntity();
        account.setUsername("test");
        account.setId(ThreadLocalRandom.current().nextInt());
        account.setPasswordHash(UUID.randomUUID().toString());
        account.setRole(AccountRole.USER);
        when(accountRepository.findByUsername(anyString())).thenReturn(Optional.of(account));
        var userDetails = userDetailsService.loadUserByUsername("test");
        assertEquals(userDetails.getUsername(), account.getUsername());
        assertEquals(userDetails.getPassword(), account.getPasswordHash());
        assertTrue(userDetails.getAuthorities().stream()
                .map(Object::toString)
                .anyMatch(authority -> authority.equals("ROLE_USER")));
    }
}
