package julia.books.domain.books;

import julia.books.security.TokenService;
import julia.books.security.UserDetailsServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringRunner.class)
@WebMvcTest(BooksController.class)
public class BooksControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    TokenService tokenService;

    @MockBean
    @Qualifier("userDetailsServiceImpl")
    UserDetailsServiceImpl userDetailsService;

    @MockBean
    BooksService booksService;

    @Test
    public void getBooks() throws Exception {
        when(booksService.getBooks(anyInt(), anyInt()))
                .thenReturn(new SearchResult<>(List.of(), 0, 10, 0));
        MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get("/books/")
                .param("page", "0")
                .param("size", "3");
        mvc.perform(get)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getBooksInvalidParams() throws Exception {
        when(booksService.getBooks(anyInt(), anyInt()))
                .thenReturn(new SearchResult<>(List.of(), 0, 10, 0));
        MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get("/books/")
                .param("page", "-1")
                .param("size", "3");
        mvc.perform(get)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    }
}
