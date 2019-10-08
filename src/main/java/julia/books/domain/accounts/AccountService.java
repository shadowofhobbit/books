package julia.books.domain.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private AccountRepository accountRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    Account registerUser(RegistrationInvoice invoice) {
        invoice.setRole(AccountRole.USER);
        return register(invoice);
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
        return Account.builder()
                .email(savedEntity.getEmail())
                .passwordHash(savedEntity.getPasswordHash())
                .username(savedEntity.getUsername())
                .role(savedEntity.getRole())
                .id(savedEntity.getId())
                .build();
    }

}
