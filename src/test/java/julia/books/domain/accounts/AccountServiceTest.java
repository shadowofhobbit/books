package julia.books.domain.accounts;

import julia.books.security.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private AccountMapper accountMapper;

    private AccountService accountService;
    private RegistrationInvoice invoice;

    @BeforeEach
    public void setUp() {
        accountService = new AccountService(accountRepository, passwordEncoder, authenticationService, accountMapper);
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

    @Test
    public void registerUsernameExists() {
        var accountEntity = new AccountEntity();
        accountEntity.setUsername("test");
        when(accountRepository.findByUsername(anyString())).thenReturn(Optional.of(accountEntity));
        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> accountService.registerUser(invoice));
    }

    @Test
    public void registerEmailExists() {
        var accountEntity = new AccountEntity();
        accountEntity.setUsername("test2");
        when(accountRepository.findByUsername(anyString())).thenReturn(Optional.of(accountEntity));
        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> accountService.registerUser(invoice));
    }
}
