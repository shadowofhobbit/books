package julia.books.domain.accounts;

import julia.books.security.AuthenticationService;
import julia.books.security.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;
    private final AccountMapper accountMapper;

    @Transactional
    public Token registerUser(RegistrationDTO invoice) {
        invoice.setRole(AccountRole.USER);
        register(invoice);
        return authenticationService.createAuthenticationToken(invoice.getUsername(), invoice.getPassword());
    }

    @Transactional
    public Account register(RegistrationDTO invoice) {
        if (accountRepository.findByUsername(invoice.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username taken");
        }
        if (accountRepository.findByEmail(invoice.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email taken");
        }
        final var accountEntity = AccountEntity.builder()
                .username(invoice.getUsername())
                .passwordHash(passwordEncoder.encode(invoice.getPassword()))
                .email(invoice.getEmail())
                .confirmedEmail(false)
                .role(invoice.getRole())
                .build();
        final AccountEntity savedEntity = accountRepository.save(accountEntity);
        log.info("Created account {}", savedEntity.getId());
        return accountMapper.toDto(savedEntity);
    }

    public Account get(int id) {
        return accountRepository.findById(id)
                .map(accountMapper::toDto)
                .orElseThrow();
    }

    @Transactional
    public Account update(Integer id, Account account) {
        final var old = accountRepository.findById(id).orElseThrow();
        old.setDescription(account.getDescription());
        old.setBirthday(account.getBirthday());
        return accountMapper.toDto(old);
    }
}
