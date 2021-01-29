package julia.books.domain.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import julia.books.domain.reviews.ReviewsService;
import julia.books.security.Token;
import julia.books.security.TokenService;
import julia.books.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest {
    @MockBean
    AccountService accountService;

    @MockBean
    ReviewsService reviewsService;

    @MockBean
    @Qualifier("userDetailsServiceImpl")
    UserDetailsServiceImpl userDetailsService;

    @MockBean
    TokenService tokenService;

    @Autowired
    private MockMvc mvc;
    private ObjectMapper objectMapper;
    private String invoiceJson;
    private RegistrationDTO registrationDTO;

    @BeforeEach
    public void setUp() {
        registrationDTO = new RegistrationDTO("test", "test@example.com", "testtest");
        objectMapper = new ObjectMapper();
    }

    @Test
    public void registerUser() throws Exception {
        registrationDTO.setRole(AccountRole.USER);
        invoiceJson = objectMapper.writeValueAsString(registrationDTO);
        var token = new Token("header.payload.sig", "");
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
        registrationDTO.setRole(AccountRole.ADMIN);
        invoiceJson = objectMapper.writeValueAsString(registrationDTO);
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
        var user = new UserDetailsServiceImpl.CustomUser(loggedInAdminDetails, 2);
        when(userDetailsService.loadUserByUsername(any())).thenReturn(user);
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
        registrationDTO.setRole(AccountRole.ADMIN);
        invoiceJson = objectMapper.writeValueAsString(registrationDTO);
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
        var user = new UserDetailsServiceImpl.CustomUser(loggedInUserDetails, 2);
        when(userDetailsService.loadUserByUsername(any())).thenReturn(user);
        MockHttpServletRequestBuilder request = post("/accounts/create")
                .header("Authorization", "Bearer something")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(invoiceJson);

        this.mvc.perform(request)
                .andExpect(status().isForbidden());
    }
}
