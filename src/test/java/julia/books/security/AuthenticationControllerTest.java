package julia.books.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import julia.books.domain.accounts.AccountRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {

    @MockBean
    @Qualifier("userDetailsServiceImpl")
    UserDetailsServiceImpl userDetailsService;

    @MockBean
    AccountRepository accountRepository;

    @MockBean
    TokenService tokenService;

    @MockBean
    AuthenticationService authenticationService;

    @Autowired
    private MockMvc mvc;

    private Token token;
    private ObjectMapper objectMapper;
    private String authInvoiceJson;
    private AuthenticationInvoice authInvoice;


    @Before
    public void setUp() throws Exception {
        authInvoice = new AuthenticationInvoice();
        authInvoice.setUsername("test");
        authInvoice.setPassword("test");
        objectMapper = new ObjectMapper();
        authInvoiceJson = objectMapper.writeValueAsString(authInvoice);
        token = new Token("header.payload.sig", "test-test");
        when(tokenService.generateToken(any())).thenReturn(token);
    }

    @Test
    public void createAuthenticationTokenUserFound() throws Exception {
        when(authenticationService.createAuthenticationToken(authInvoice.getUsername(), authInvoice.getPassword()))
                .thenReturn(token);
        MockHttpServletRequestBuilder request = post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(authInvoiceJson);

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(token)));

    }

    @Test
    public void createAuthenticationTokenUserNotFound() throws Exception {
        when(authenticationService.createAuthenticationToken(authInvoice.getUsername(), authInvoice.getPassword()))
                .thenThrow(UsernameNotFoundException.class);
        MockHttpServletRequestBuilder request = post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(authInvoiceJson);

        this.mvc.perform(request)
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void createAuthenticationTokenWrongPassword() throws Exception {
        when(authenticationService.createAuthenticationToken(authInvoice.getUsername(), authInvoice.getPassword()))
                .thenThrow(BadCredentialsException.class);

        MockHttpServletRequestBuilder request = post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(authInvoiceJson);

        this.mvc.perform(request)
                .andExpect(status().isUnauthorized());

    }
}
