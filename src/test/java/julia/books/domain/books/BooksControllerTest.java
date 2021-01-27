package julia.books.domain.books;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BooksController.class)
public class BooksControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    TokenService tokenService;

    @MockBean
    @Qualifier("userDetailsServiceImpl")
    UserDetailsServiceImpl userDetailsService;

    @MockBean
    BooksService booksService;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser
    public void addBook() throws JsonProcessingException {
        var book = new Book();
        book.setId(1L);
        book.setTitle("Война и мир");
        book.setAuthor("Толстой");
        book.setLanguage("Ru");
        var bookInvoice = new BookInvoice();
        bookInvoice.setTitle("Война и мир");
        bookInvoice.setAuthor("Толстой");
        bookInvoice.setLanguage("Ru");
        String json = objectMapper.writeValueAsString(bookInvoice);
        when(booksService.create(bookInvoice)).thenReturn(book);
        var post = MockMvcRequestBuilders.post("/books/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        try {
            mvc.perform(post)
                    .andExpect(status().isCreated())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(book)));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void addBookUnauthorized() throws Exception {
        when(booksService.create(any()))
                .thenReturn(new Book());
        var bookInvoice = new BookInvoice();
        bookInvoice.setTitle("Война и мир");
        bookInvoice.setAuthor("Толстой");
        bookInvoice.setLanguage("Ru");
        String json = objectMapper.writeValueAsString(bookInvoice);
        var post = MockMvcRequestBuilders.post("/books/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        assertThrows(NestedServletException.class, () -> mvc.perform(post));
    }

    @Test
    @WithMockUser
    public void addBookWithoutTitle() throws JsonProcessingException {
        when(booksService.create(any()))
                .thenReturn(new Book());
        var bookInvoice = new BookInvoice();
        bookInvoice.setTitle("");
        bookInvoice.setAuthor("Толстой");
        bookInvoice.setLanguage("Ru");
        String json = objectMapper.writeValueAsString(bookInvoice);
        var post = MockMvcRequestBuilders.post("/books/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        try {
            mvc.perform(post)
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

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

    @Test
    public void getBook() throws Exception {
        var book = new Book();
        book.setId(1L);
        book.setTitle("1984");
        book.setAuthor("Оруэлл");
        when(booksService.get(1))
                .thenReturn(Optional.of(book));
        var json = objectMapper.writeValueAsString(book);
        MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get("/books/1");
        mvc.perform(get)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(json));
    }

    @Test
    public void getBookNotFound() throws Exception {
        when(booksService.get(anyLong()))
                .thenReturn(Optional.empty());
        MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get("/books/1");
        mvc.perform(get).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void updateBook() throws JsonProcessingException {
        var bookInvoice = new BookInvoice();
        bookInvoice.setId(1L);
        bookInvoice.setTitle("Война и мир");
        bookInvoice.setAuthor("Толстой");
        bookInvoice.setLanguage("Ru");
        String json = objectMapper.writeValueAsString(bookInvoice);
        var put = MockMvcRequestBuilders.put("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        try {
            mvc.perform(put)
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteBookByAdmin() throws Exception {
        var deleteRequest = MockMvcRequestBuilders.delete("/books/{id}", 1);
        mvc.perform(deleteRequest).andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void deleteBookByNormalUser() {
        var deleteRequest = MockMvcRequestBuilders.delete("/books/{id}", 1);
        assertThrows(NestedServletException.class,
                () -> mvc.perform(deleteRequest));
    }
}
