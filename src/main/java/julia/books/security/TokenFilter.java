package julia.books.security;

import io.jsonwebtoken.ExpiredJwtException;
import julia.books.domain.accounts.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Log4j2
public class TokenFilter extends OncePerRequestFilter {
    private static final String HEADER_PREFIX = "Bearer ";
    private static final String HEADER_NAME = "Authorization";
    private final UserService userService;
    private final TokenService tokenService;

    @Autowired
    public TokenFilter(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        var authHeader = request.getHeader(HEADER_NAME);
        UsernamePasswordAuthenticationToken authentication = null;
        if (authHeader != null && authHeader.startsWith(HEADER_PREFIX)) {
            String token = authHeader.substring(HEADER_PREFIX.length());
            try {
                var username = tokenService.getUsernameFromToken(token);
                UserDetails userDetails = this.userService.loadUserByUsername(username);
                authentication = new UsernamePasswordAuthenticationToken(userDetails,
                        null,
                        userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            } catch (IllegalArgumentException e) {
                log.error("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                log.error("JWT Token has expired");
            }
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
