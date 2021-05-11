package julia.books.domain.quotes;

import com.fasterxml.jackson.databind.ObjectMapper;
import julia.books.domain.books.SearchResult;
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
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(QuotesController.class)
@ContextConfiguration
class QuotesControllerTest {
    @MockBean
    private QuotesService quotesService;

    @MockBean
    @Qualifier("userDetailsServiceImpl")
    UserDetailsServiceImpl userDetailsService;

    @MockBean
    TokenService tokenService;

    @Autowired
    private WebApplicationContext webApplicationContext;


    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void create() throws Exception {
        var requestDto = prepareDto(null, 2, 1, "test");
        var fixedRequestDto = prepareDto(null, 2, 2, "test");
        var responseDto = prepareDto(1L, 2, 2, "test");
        var json = objectMapper.writeValueAsString(requestDto);
        var expectedJson = objectMapper.writeValueAsString(responseDto);
        when(quotesService.add(fixedRequestDto)).thenReturn(responseDto);
        mockUserWithId(2);
        var request = MockMvcRequestBuilders.post("/books/2/quotes/")
                .header("Authorization", "Bearer test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    private void mockUserWithId(int id) {
        var userDetails = User.withUsername("rob")
                .password("qwerty")
                .roles("USER")
                .build();
        var principal = new UserDetailsServiceImpl.CustomUser(userDetails, id);
        when(userDetailsService.loadUserByUsername(any()))
                .thenReturn(principal);
    }

    @Test
    void createUnauthorized() throws Exception {
        var requestDto = prepareDto(null, 2, 3, "hi");
        var responseDto = prepareDto(1L, 2, 3, "hi");
        var json = objectMapper.writeValueAsString(requestDto);
        when(quotesService.add(requestDto)).thenReturn(responseDto);
        var request = MockMvcRequestBuilders.post("/books/2/quotes/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createWithEmptyContent() throws Exception {
        var requestDto = prepareDto(null, 2, 3, "");
        var json = objectMapper.writeValueAsString(requestDto);
        mockUserWithId(3);
        var request = MockMvcRequestBuilders.post("/books/2/quotes/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getQuoteById() throws Exception {
        long id = 1;
        var dto = prepareDto(id, 2, 3, "thanks for all the fish");
        var expectedJson = objectMapper.writeValueAsString(dto);
        when(quotesService.getById(id)).thenReturn(Optional.of(dto));
        var request = MockMvcRequestBuilders.get("/quotes/1");
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    private QuoteDTO prepareDto(Long id,
                                 long bookId,
                                 int readerId,
                                 String content) {
        var dto = new QuoteDTO();
        dto.setId(id);
        dto.setBookId(bookId);
        dto.setReaderId(readerId);
        dto.setContent(content);
        return dto;
    }

    @Test
    void getQuoteByIdNotFound() throws Exception {
        long id = 1;
        when(quotesService.getById(id)).thenReturn(Optional.empty());
        var request = MockMvcRequestBuilders.get("/quotes/1");
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getQuotes() throws Exception {
        var dto = prepareDto(1L, 2, 3, "42");
        var dto2 = prepareDto(3L, 2, 3, "42");
        var searchResult = new SearchResult<>(List.of(dto, dto2), 0, 10, 20);
        var expectedJson = objectMapper.writeValueAsString(searchResult);
        when(quotesService.getQuotesForBook(2, 0, 10)).thenReturn(searchResult);
        var request = MockMvcRequestBuilders.get("/books/2/quotes/")
                .queryParam("page", "0")
                .queryParam("size", "10");
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void getQuotesBadPageNumber() throws Exception {
        var request = MockMvcRequestBuilders.get("/books/2/quotes/")
                .queryParam("page", "-2")
                .queryParam("size", "10");
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void update() throws Exception {
        var requestDto = prepareDto(10L, 2, 1, "hi");
        var responseDto = prepareDto(10L, 2, 1, "hi");
        var json = objectMapper.writeValueAsString(requestDto);
        var expectedJson = objectMapper.writeValueAsString(responseDto);
        when(quotesService.update(requestDto)).thenReturn(responseDto);
        mockUserWithId(1);
        var request = MockMvcRequestBuilders.put("/quotes/10")
                .header("Authorization", "Bearer test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    @Test
    void updateWithInvalidContent() throws Exception {
        var requestDto = prepareDto(10L, 2, 1, "");
        var json = objectMapper.writeValueAsString(requestDto);
        mockUserWithId(1);
        var request = MockMvcRequestBuilders.put("/quotes/10")
                .header("Authorization", "Bearer test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUnauthorized() throws Exception {
        var requestDto = prepareDto(1L, 2, 3, "test");
        var json = objectMapper.writeValueAsString(requestDto);
        var request = MockMvcRequestBuilders.put("/quotes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUnauthorized() throws Exception {
        var request = MockMvcRequestBuilders.delete("/quotes/7");
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }
}
