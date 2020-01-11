package julia.books.domain.accounts;

import julia.books.security.AuthenticationService;
import julia.books.security.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private AccountRepository accountRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationService authenticationService;
    private AccountMapper accountMapper;

    @Autowired
    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder, AuthenticationService authenticationService, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationService = authenticationService;
        this.accountMapper = accountMapper;
    }

    Token registerUser(RegistrationInvoice invoice) {
        invoice.setRole(AccountRole.USER);
        register(invoice);
        return authenticationService.createAuthenticationToken(invoice.getUsername(), invoice.getPassword());
    }

    Account register(RegistrationInvoice invoice) {
        if (accountRepository.findByUsername(invoice.getUsername()).isPresent()) {
            throw new RuntimeException("Username taken");
        }
        if (accountRepository.findByEmail(invoice.getEmail()).isPresent()) {
            throw new RuntimeException("Email taken");
        }
        var accountEntity = AccountEntity.builder()
                .username(invoice.getUsername())
                .passwordHash(passwordEncoder.encode(invoice.getPassword()))
                .email(invoice.getEmail())
                .role(invoice.getRole())
                .build();
        AccountEntity savedEntity = accountRepository.save(accountEntity);
        return accountMapper.toDto(savedEntity);
    }

}
