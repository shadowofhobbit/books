package julia.books.domain.quotes;

import julia.books.domain.accounts.AccountEntity;
import julia.books.domain.accounts.AccountRepository;
import julia.books.domain.books.BookEntity;
import julia.books.domain.books.BooksRepository;
import julia.books.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.NoSuchElementException;
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
    private QuoteEntity quoteEntity2;
    private QuoteDTO expectedDto2;

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
        quoteEntity2 = new QuoteEntity();
        quoteEntity2.setId(2L);
        quoteEntity2.setContent("Test2");
        quoteEntity2.setBook(bookEntity);
        quoteEntity2.setReader(accountEntity);
        expectedDto = new QuoteDTO();
        expectedDto.setId(1L);
        expectedDto.setBookId(2L);
        expectedDto.setContent("Test");
        expectedDto.setReaderId(3);
        expectedDto2 = new QuoteDTO();
        expectedDto2.setId(2L);
        expectedDto2.setBookId(2L);
        expectedDto2.setContent("Test");
        expectedDto2.setReaderId(3);
    }

    @Test
    void add() {
        var dto = new QuoteDTO();
        dto.setContent("Test");
        dto.setBookId(2L);
        dto.setReaderId(3);
        var entity = new QuoteEntity();
        entity.setContent("Test");
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(booksRepository.getOne(2L)).thenReturn(bookEntity);
        when(accountRepository.getOne(3)).thenReturn(accountEntity);
        quotesService.add(dto);
        verify(mapper).toEntity(dto);
        verify(booksRepository).getOne(2L);
        verify(accountRepository).getOne(3);
        verify(quotesRepository).save(any());
        verify(mapper).toDto(any());
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
    void getQuotesForBook() {
        when(quotesRepository.findByBookId(anyLong(), any())).thenReturn(
                new PageImpl<>(List.of(quoteEntity, quoteEntity2), PageRequest.of(0, 10), 2));
        when(mapper.toDto(quoteEntity)).thenReturn(expectedDto);
        when(mapper.toDto(quoteEntity2)).thenReturn(expectedDto2);
        var searchResult = quotesService.getQuotesForBook(2L, 0, 10);
        verify(quotesRepository).findByBookId(eq(2L), any());
        assertEquals(List.of(expectedDto, expectedDto2), searchResult.getContent());
        assertEquals(0, searchResult.getPage());
        assertEquals(10, searchResult.getSize());
        assertEquals(2, searchResult.getTotalElements());
    }

    @Test
    void getQuotesByUserId() {
        when(quotesRepository.findByReaderId(anyInt(), any())).thenReturn(
                new PageImpl<>(List.of(quoteEntity, quoteEntity2), PageRequest.of(0, 10), 2));
        when(mapper.toDto(quoteEntity)).thenReturn(expectedDto);
        when(mapper.toDto(quoteEntity2)).thenReturn(expectedDto2);
        var searchResult = quotesService.getQuotesByUserId(3, 0, 10);
        verify(quotesRepository).findByReaderId(eq(3), any());
        assertEquals(List.of(expectedDto, expectedDto2), searchResult.getContent());
        assertEquals(0, searchResult.getPage());
        assertEquals(10, searchResult.getSize());
        assertEquals(2, searchResult.getTotalElements());
    }

    @Test
    void update() {
        var dto = new QuoteDTO();
        dto.setId(1L);
        dto.setContent("Updated test");
        dto.setBookId(2L);
        dto.setReaderId(3);
        var updatedDto = new QuoteDTO();
        updatedDto.setId(1L);
        updatedDto.setContent("Updated test");
        updatedDto.setBookId(2L);
        updatedDto.setReaderId(3);
        when(quotesRepository.findById(1L)).thenReturn(Optional.of(quoteEntity));
        when(mapper.toDto(any())).thenReturn(updatedDto);
        quotesService.update(dto);
        var captor = ArgumentCaptor.forClass(QuoteEntity.class);
        verify(quotesRepository).save(captor.capture());
        assertEquals("Updated test", captor.getValue().getContent());
    }

    @Test
    void updateOtherUserQuote() {
        var dto = new QuoteDTO();
        dto.setId(1L);
        dto.setContent("Updated test");
        dto.setBookId(2L);
        dto.setReaderId(4);
        when(quotesRepository.findById(1L)).thenReturn(Optional.of(quoteEntity));
        assertThrows(AccessDeniedException.class, () -> quotesService.update(dto));
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
        assertThrows(AccessDeniedException.class, () -> quotesService.delete(1L, customUser));
        verifyNoMoreInteractions(quotesRepository);
    }

    @Test
    void publishNonExistingQuote() {
        when(quotesRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> quotesService.publishInTelegramBot(1L));
    }

}
