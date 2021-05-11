package julia.books.domain.quotes;

import julia.books.domain.accounts.AccountEntity;
import julia.books.domain.accounts.AccountRepository;
import julia.books.domain.books.BookEntity;
import julia.books.domain.books.BooksRepository;
import julia.books.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuotesServiceTest {
    @Mock
    private QuotesMapper mapper;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private QuotesRepository quotesRepository;
    @Mock
    private BooksRepository booksRepository;
    private QuotesService quotesService;
    private QuoteEntity quoteEntity;
    private BookEntity bookEntity;
    private AccountEntity accountEntity;
    private QuoteDTO expectedDto;

    @BeforeEach
    void setUp() {
        quotesService = new QuotesService(mapper, accountRepository, quotesRepository, booksRepository);
        prepareData();
    }

    private void prepareData() {
        quoteEntity = new QuoteEntity();
        quoteEntity.setId(1L);
        quoteEntity.setContent("Test");
        bookEntity = new BookEntity();
        bookEntity.setId(2L);
        accountEntity = new AccountEntity();
        accountEntity.setId(3);
        quoteEntity.setBook(bookEntity);
        quoteEntity.setReader(accountEntity);
        expectedDto = new QuoteDTO();
        expectedDto.setId(1L);
        expectedDto.setBookId(2L);
        expectedDto.setContent("Test");
        expectedDto.setReaderId(3);
    }

    @Test
    void getByIdNotFound() {
        when(quotesRepository.findById(1L)).thenReturn(Optional.empty());
        var quoteDTO = quotesService.getById(1L);
        verify(quotesRepository).findById(1L);
        assertTrue(quoteDTO.isEmpty());
    }

    @Test
    void getById() {
        when(quotesRepository.findById(1L)).thenReturn(Optional.of(quoteEntity));
        when(mapper.toDto(quoteEntity)).thenReturn(expectedDto);
        var actualDto = quotesService.getById(1L);
        verify(quotesRepository).findById(1L);
        verify(mapper).toDto(quoteEntity);
        assertEquals(expectedDto, actualDto.orElseThrow());
    }

    @Test
    void deleteByAdmin() {
        var customUser = new UserDetailsServiceImpl.CustomUser(User.withUsername("user")
                .password("password")
                .roles("ADMIN")
                .build(), 3);
        quotesService.delete(1L, customUser);
        verify(quotesRepository).deleteById(1L);
    }

    @Test
    void deleteUserQuote() {
        var customUser = new UserDetailsServiceImpl.CustomUser(User.withUsername("user")
                .password("password")
                .roles("USER")
                .build(), 3);
        when(quotesRepository.findById(1L)).thenReturn(Optional.of(quoteEntity));
        quotesService.delete(1L, customUser);
        verify(quotesRepository).deleteById(1L);
    }

    @Test
    void deleteOtherUserQuote() {
        var customUser = new UserDetailsServiceImpl.CustomUser(User.withUsername("user")
                .password("password")
                .roles("USER")
                .build(), 2);
        when(quotesRepository.findById(1L)).thenReturn(Optional.of(quoteEntity));
        assertThrows(HttpClientErrorException.class, () -> quotesService.delete(1L, customUser));
        verifyNoMoreInteractions(quotesRepository);
    }

}
