package julia.books.domain.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import julia.books.security.Token;
import julia.books.security.TokenService;
import julia.books.security.UserDetailsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest {
    @MockBean
    AccountService accountService;

    @MockBean
    @Qualifier("userDetailsServiceImpl")
    UserDetailsServiceImpl userDetailsService;

    @MockBean
    TokenService tokenService;

    @Autowired
    private MockMvc mvc;
    private ObjectMapper objectMapper;
    private String invoiceJson;
    private RegistrationInvoice registrationInvoice;

    @Before
    public void setUp() {
        registrationInvoice = new RegistrationInvoice("test", "test@example.com", "testtest");
        objectMapper = new ObjectMapper();
    }

    @Test
    public void registerUser() throws Exception {
        registrationInvoice.setRole(AccountRole.USER);
        invoiceJson = objectMapper.writeValueAsString(registrationInvoice);
        var token = new Token("header.payload.sig");
        when(accountService.registerUser(any())).thenReturn(token);
        MockHttpServletRequestBuilder request = post("/accounts/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(invoiceJson);

        this.mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(token)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void createAdminWithAdminRole() throws Exception {
        registrationInvoice.setRole(AccountRole.ADMIN);
        invoiceJson = objectMapper.writeValueAsString(registrationInvoice);
        var account = Account.builder()
                .id(1)
                .username("test")
                .email("test@example.com")
                .role(AccountRole.ADMIN)
                .build();
        when(accountService.register(any())).thenReturn(account);
        var loggedInAdminDetails = User.withUsername("admin")
                .password("hashhashhash")
                .roles(AccountRole.ADMIN.name())
                .build();
        when(userDetailsService.loadUserByUsername(any())).thenReturn(loggedInAdminDetails);
        MockHttpServletRequestBuilder request = post("/accounts/create")
                .header("Authorization", "Bearer something")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(invoiceJson);

        this.mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(account)));
    }

    @Test
    @WithMockUser
    public void createAdminWithUserAuthority() throws Exception {
        registrationInvoice.setRole(AccountRole.ADMIN);
        invoiceJson = objectMapper.writeValueAsString(registrationInvoice);
        var account = Account.builder()
                .id(1)
                .username("test")
                .email("test@example.com")
                .role(AccountRole.ADMIN)
                .build();
        when(accountService.register(any())).thenReturn(account);
        var loggedInUserDetails = User.withUsername("normal user")
                .password("hashhashhash")
                .authorities(AccountRole.USER)
                .build();
        when(userDetailsService.loadUserByUsername(any())).thenReturn(loggedInUserDetails);
        MockHttpServletRequestBuilder request = post("/accounts/create")
                .header("Authorization", "Bearer something")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(invoiceJson);

        this.mvc.perform(request)
                .andExpect(status().isForbidden());
    }
}
