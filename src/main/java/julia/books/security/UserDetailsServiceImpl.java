package julia.books.security;

import julia.books.domain.accounts.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    public CustomUser loadUserByUsername(String username) throws UsernameNotFoundException {
        final var account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
        final UserDetails userDetails = User.withUsername(account.getUsername())
                .password(account.getPasswordHash())
                .roles(account.getRole().name())
                .build();
        return new CustomUser(userDetails, account.getId());
    }

    public static class CustomUser implements UserDetails {
        private static final long serialVersionUID = 0L;
        private final Integer id;
        private final UserDetails user;

        public CustomUser(UserDetails userDetails, Integer id) {
            this.id = id;
            this.user = userDetails;
        }


        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return user.getAuthorities();
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getUsername();
        }

        @Override
        public boolean isAccountNonExpired() {
            return user.isAccountNonExpired();
        }

        @Override
        public boolean isAccountNonLocked() {
            return user.isAccountNonLocked();
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return user.isCredentialsNonExpired();
        }

        @Override
        public boolean isEnabled() {
            return user.isEnabled();
        }

        public Integer getId() {
            return id;
        }
    }
}
