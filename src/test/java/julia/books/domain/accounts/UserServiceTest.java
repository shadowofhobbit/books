package julia.books.domain.accounts;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class UserServiceTest {
    @Mock
    private AccountRepository accountRepository;

    private UserService userService;

    @Before
    public void setUp() {
        userService = new UserService(accountRepository);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsernameNoUser() {
        when(accountRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        userService.loadUserByUsername("test");
    }

    @Test
    public void loadUserByUsernameSuccessfully() {
        var account = new Account();
        account.setUsername("test");
        account.setId(ThreadLocalRandom.current().nextInt());
        account.setPasswordHash(UUID.randomUUID().toString());
        account.setRole(AccountRole.USER);
        when(accountRepository.findByUsername(anyString())).thenReturn(Optional.of(account));
        var userDetails = userService.loadUserByUsername("test");
        assertEquals(userDetails.getUsername(), account.getUsername());
        assertEquals(userDetails.getPassword(), account.getPasswordHash());
        assertTrue(userDetails.getAuthorities().contains(account.getRole()));
    }
}