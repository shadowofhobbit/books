package julia.books.domain.news;

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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(NewsController.class)
class NewsControllerTest {
    @MockBean
    private NewsService newsService;

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
        var requestDto = prepareDto(null, 2, "test", "test", null);
        var fixedRequestDto = prepareDto(null, 2, "test", "test", null);
        var responseDto = prepareDto(1L, 2, "test", "test", Instant.now());
        var json = objectMapper.writeValueAsString(requestDto);
        var expectedJson = objectMapper.writeValueAsString(responseDto);
        when(newsService.add(fixedRequestDto)).thenReturn(responseDto);
        mockAdminWithId(2);
        var request = MockMvcRequestBuilders.post("/news/")
                .header("Authorization", "Bearer test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    private void mockAdminWithId(int id) {
        var userDetails = User.withUsername("rob")
                .password("qwerty")
                .roles("ADMIN")
                .build();
        var principal = new UserDetailsServiceImpl.CustomUser(userDetails, id);
        when(userDetailsService.loadUserByUsername(any()))
                .thenReturn(principal);
    }

    @Test
    void createUnauthorized() throws Exception {
        var requestDto = prepareDto(null, 2, "Hi", "hi", null);
        //var responseDto = prepareDto(1L, 2, 3, "hi");
        var json = objectMapper.writeValueAsString(requestDto);
       // when(newsService.add(requestDto)).thenReturn(responseDto);
        var request = MockMvcRequestBuilders.post("/news/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createWithEmptyContent() throws Exception {
        var requestDto = prepareDto(null, 2, "t", "", null);
        var json = objectMapper.writeValueAsString(requestDto);
        mockAdminWithId(3);
        var request = MockMvcRequestBuilders.post("/news/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNewsById() throws Exception {
        long id = 1;
        var dto = prepareDto(id, 2, "Thanks", "thanks for all the fish",
                Instant.now().minus(1, ChronoUnit.DAYS));
        var expectedJson = objectMapper.writeValueAsString(dto);
        when(newsService.getById(id)).thenReturn(Optional.of(dto));
        var request = MockMvcRequestBuilders.get("/news/1");
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    private NewsDTO prepareDto(Long id,
                               int authorId,
                               String title,
                               String content,
                               Instant date) {
        var dto = new NewsDTO();
        dto.setId(id);
        dto.setAuthorId(authorId);
        dto.setTitle(title);
        dto.setContent(content);
        dto.setDate(date);
        return dto;
    }

    @Test
    void getNewsByIdNotFound() throws Exception {
        long id = 1;
        when(newsService.getById(id)).thenReturn(Optional.empty());
        var request = MockMvcRequestBuilders.get("/news/1");
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getNews() throws Exception {
        var dto = prepareDto(1L, 2, "42", "42",
                Instant.now().minus(2, ChronoUnit.DAYS));
        var dto2 = prepareDto(3L, 2, "again 42", "42",
                Instant.now().minus(2, ChronoUnit.HOURS));
        var searchResult = new SearchResult<>(List.of(dto, dto2), 0, 10, 2);
        var expectedJson = objectMapper.writeValueAsString(searchResult);
        when(newsService.getNews(0, 10)).thenReturn(searchResult);
        var request = MockMvcRequestBuilders.get("/news/")
                .queryParam("page", "0")
                .queryParam("size", "10");
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void getNewsBadPageNumber() throws Exception {
        var request = MockMvcRequestBuilders.get("/news/")
                .queryParam("page", "-2")
                .queryParam("size", "10");
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void update() throws Exception {
        var requestDto = prepareDto(10L, 2, "Hi", "hi",
                Instant.now().minus(1, ChronoUnit.HOURS));
        var responseDto = prepareDto(10L, 2, "Hi", "hi",
                Instant.now().minus(1, ChronoUnit.HOURS));
        var json = objectMapper.writeValueAsString(requestDto);
        var expectedJson = objectMapper.writeValueAsString(responseDto);
        when(newsService.update(requestDto)).thenReturn(responseDto);
        mockAdminWithId(1);
        var request = MockMvcRequestBuilders.put("/news/10")
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
        var requestDto = prepareDto(10L, 2, "t", "", null);
        var json = objectMapper.writeValueAsString(requestDto);
        mockAdminWithId(1);
        var request = MockMvcRequestBuilders.put("/news/10")
                .header("Authorization", "Bearer test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUnauthorized() throws Exception {
        var requestDto = prepareDto(1L, 2, "t", "test", null);
        var json = objectMapper.writeValueAsString(requestDto);
        var request = MockMvcRequestBuilders.put("/news/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUnauthorized() throws Exception {
        var request = MockMvcRequestBuilders.delete("/news/7");
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }
}
