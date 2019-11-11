package julia.books.domain.accounts;

import julia.books.security.AuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationService authenticationService;

    private AccountService accountService;
    private RegistrationInvoice invoice;

    @Before
    public void setUp() {
        accountService = new AccountService(accountRepository, passwordEncoder, authenticationService);
        invoice = new RegistrationInvoice("test", "test@example.com", "testQwerty");
        invoice.setRole(AccountRole.ADMIN);
    }

    @Test
    public void registerUser() {
        when(accountRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        ArgumentCaptor<AccountEntity> captor = ArgumentCaptor.forClass(AccountEntity.class);
        when(accountRepository.save(any())).thenReturn(new AccountEntity());
        accountService.registerUser(invoice);
        verify(accountRepository).save(captor.capture());
        assertEquals(captor.getValue().getRole(), AccountRole.USER);
    }

    @Test(expected = RuntimeException.class)
    public void registerUsernameExists() {
        var accountEntity = new AccountEntity();
        accountEntity.setUsername("test");
        when(accountRepository.findByUsername(anyString())).thenReturn(Optional.of(accountEntity));
        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        accountService.registerUser(invoice);
    }

    @Test(expected = RuntimeException.class)
    public void registerEmailExists() {
        var accountEntity = new AccountEntity();
        accountEntity.setUsername("test2");
        when(accountRepository.findByUsername(anyString())).thenReturn(Optional.of(accountEntity));
        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        accountService.registerUser(invoice);
    }
}
