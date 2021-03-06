package julia.books.testsecurity;

import julia.books.security.UserDetailsServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithSecurityContextFactory;


public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {
    private static final Logger log = LogManager.getLogger(WithMockCustomUserSecurityContextFactory.class);

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        log.info("creating test security context");
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        var userDetails = User.withUsername(customUser.username())
                .password("qwerty")
                .roles("USER")
                .build();
        var principal = new UserDetailsServiceImpl.CustomUser(userDetails, customUser.id());
        var auth = new UsernamePasswordAuthenticationToken(principal,
                null,
                principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
