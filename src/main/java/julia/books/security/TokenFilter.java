package julia.books.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
@Component
@Log4j2
public class TokenFilter extends OncePerRequestFilter {
    private static final String HEADER_PREFIX = "Bearer ";
    private static final String HEADER_NAME = "Authorization";
    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;

    @Autowired
    public TokenFilter(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService, TokenService tokenService) {
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final var authHeader = request.getHeader(HEADER_NAME);
        UsernamePasswordAuthenticationToken authentication = null;
        if (authHeader != null && authHeader.startsWith(HEADER_PREFIX)) {
            final String token = authHeader.substring(HEADER_PREFIX.length());
            try {
                final var username = tokenService.getUsernameFromToken(token);
                final UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                authentication = new UsernamePasswordAuthenticationToken(userDetails,
                        null,
                        userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            } catch (Exception e) {
                log.error(e);
            }
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
