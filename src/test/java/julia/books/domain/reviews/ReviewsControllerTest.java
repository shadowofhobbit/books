package julia.books.domain.reviews;

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

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ReviewsController.class)
@ContextConfiguration
class ReviewsControllerTest {
    @MockBean
    private ReviewsService reviewsService;

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
        var requestDto = prepareDto(null, 2, 1, 7, "Interesting", "An interesting book", null);
        var fixedRequestDto = prepareDto(null, 2, 2, 7, "Interesting", "An interesting book", null);
        var responseDto = prepareDto(1L, 2, 2, 7, "Interesting", "An interesting book", Instant.now());
        var json = objectMapper.writeValueAsString(requestDto);
        var expectedJson = objectMapper.writeValueAsString(responseDto);
        when(reviewsService.add(fixedRequestDto)).thenReturn(responseDto);
        mockUserWithId(2);
        var request = MockMvcRequestBuilders.post("/books/2/reviews/")
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
        ReviewDTO requestDto = prepareDto(null, 2, 3, 7, "Interesting", "An interesting book", null);
        ReviewDTO responseDto = prepareDto(1L, 2, 3, 7, "Interesting", "An interesting book", Instant.now());
        var json = objectMapper.writeValueAsString(requestDto);
        when(reviewsService.add(requestDto)).thenReturn(responseDto);
        var request = MockMvcRequestBuilders.post("/books/2/reviews/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createWithEmptyContent() throws Exception {
        ReviewDTO requestDto = prepareDto(null, 2, 3, 7, "Interesting", "", null);
        var json = objectMapper.writeValueAsString(requestDto);
        mockUserWithId(3);
        var request = MockMvcRequestBuilders.post("/books/2/reviews/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getReviewById() throws Exception {
        long id = 1;
        ReviewDTO dto = prepareDto(id, 2, 3, 7, "Interesting", "An interesting book", Instant.now());
        var expectedJson = objectMapper.writeValueAsString(dto);
        when(reviewsService.getById(id)).thenReturn(Optional.of(dto));
        var request = MockMvcRequestBuilders.get("/books/2/reviews/1");
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    private ReviewDTO prepareDto(Long id,
                                 long bookId,
                                 int reviewerId,
                                 int rating,
                                 String title,
                                 String content,
                                 Instant date) {
        var dto = new ReviewDTO();
        dto.setId(id);
        dto.setBookId(bookId);
        dto.setReviewerId(reviewerId);
        dto.setRating(rating);
        dto.setTitle(title);
        dto.setContent(content);
        dto.setDate(date);
        return dto;
    }

    @Test
    void getReviewByIdNotFound() throws Exception {
        long id = 1;
        when(reviewsService.getById(id)).thenReturn(Optional.empty());
        var request = MockMvcRequestBuilders.get("/books/2/reviews/1");
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getReviews() throws Exception {
        ReviewDTO dto = prepareDto(1L, 2, 3, 7, "Interesting", "An interesting book", Instant.now());
        ReviewDTO dto2 = prepareDto(3L, 2, 3, 7, "Interesting", "An interesting book", Instant.now());
        var searchResult = new SearchResult<>(List.of(dto, dto2), 0, 10, 20);
        var expectedJson = objectMapper.writeValueAsString(searchResult);
        when(reviewsService.getReviewsForBook(2, 0, 10)).thenReturn(searchResult);
        var request = MockMvcRequestBuilders.get("/books/2/reviews/")
                .queryParam("page", "0")
                .queryParam("size", "10");
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void getReviewsBadPageNumber() throws Exception {
        var request = MockMvcRequestBuilders.get("/books/2/reviews/")
                .queryParam("page", "-2")
                .queryParam("size", "10");
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void update() throws Exception {
        var requestDto = prepareDto(10L, 2, 1, 7, "Interesting", "An interesting book", null);
        var responseDto = prepareDto(10L, 2, 1, 7, "Interesting", "An interesting book", Instant.now());
        var json = objectMapper.writeValueAsString(requestDto);
        var expectedJson = objectMapper.writeValueAsString(responseDto);
        when(reviewsService.update(requestDto)).thenReturn(responseDto);
        mockUserWithId(1);
        var request = MockMvcRequestBuilders.put("/books/2/reviews/10")
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
    void updateWithInvalidRating() throws Exception {
        var requestDto = prepareDto(10L, 2, 1, 17, "Interesting", "An interesting book", null);
        var json = objectMapper.writeValueAsString(requestDto);
        mockUserWithId(1);
        var request = MockMvcRequestBuilders.put("/books/2/reviews/10")
                .header("Authorization", "Bearer test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUnauthorized() throws Exception {
        ReviewDTO requestDto = prepareDto(1L, 2, 3, 7, "Interesting", "An interesting book", null);
        var json = objectMapper.writeValueAsString(requestDto);
        var request = MockMvcRequestBuilders.put("/books/2/reviews/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUnauthorized() throws Exception {
        var request = MockMvcRequestBuilders.delete("/books/2/reviews/7");
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }
}
