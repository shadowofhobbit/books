package julia.books.domain.news;

import julia.books.domain.accounts.AccountEntity;
import julia.books.domain.accounts.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceTest {
    @Mock
    private NewsMapper mapper;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private NewsRepository newsRepository;
    private NewsService newsService;

    private NewsEntity newsEntity;
    private AccountEntity accountEntity;
    private NewsDTO expectedDto;

    @BeforeEach
    void setUp() {
        newsService = new NewsService(mapper, newsRepository, accountRepository);
        prepareData();
    }

    private void prepareData() {
        newsEntity = new NewsEntity();
        newsEntity.setId(1L);
        newsEntity.setTitle("Test");
        newsEntity.setContent("Test");
        accountEntity = new AccountEntity();
        accountEntity.setId(3);
        newsEntity.setAuthor(accountEntity);
        expectedDto = new NewsDTO();
        expectedDto.setId(1L);
        expectedDto.setTitle("Test");
        expectedDto.setContent("Test");
        expectedDto.setAuthorId(3);
    }

    @Test
    void add() {
        var dto = new NewsDTO();
        dto.setTitle("Test");
        dto.setContent("Test");
        dto.setAuthorId(3);
        var entity = new NewsEntity();
        entity.setTitle("Test");
        entity.setContent("Test");
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(accountRepository.getOne(3)).thenReturn(accountEntity);
        newsService.add(dto);
        var captor = ArgumentCaptor.forClass(NewsDTO.class);
        verify(mapper).toEntity(captor.capture());
        assertNotNull(captor.getValue().getDate());
        verify(accountRepository).getOne(3);
        verify(newsRepository).save(any());
        verify(mapper).toDto(any());
    }

    @Test
    void getByIdNotFound() {
        when(newsRepository.findById(1L)).thenReturn(Optional.empty());
        var quoteDTO = newsService.getById(1L);
        verify(newsRepository).findById(1L);
        assertTrue(quoteDTO.isEmpty());
    }

    @Test
    void getById() {
        when(newsRepository.findById(1L)).thenReturn(Optional.of(newsEntity));
        when(mapper.toDto(newsEntity)).thenReturn(expectedDto);
        var actualDto = newsService.getById(1L);
        verify(newsRepository).findById(1L);
        verify(mapper).toDto(newsEntity);
        assertEquals(expectedDto, actualDto.orElseThrow());
    }

    @Test
    void update() {
        var dto = new NewsDTO();
        dto.setId(1L);
        dto.setTitle("Spring Update");
        dto.setContent("We fixed bugs");
        dto.setAuthorId(3);
        when(newsRepository.findById(1L)).thenReturn(Optional.of(newsEntity));
        when(mapper.toDto(any())).thenReturn(dto);
        newsService.update(dto);
        var captor = ArgumentCaptor.forClass(NewsEntity.class);
        verify(newsRepository).save(captor.capture());
        assertEquals("We fixed bugs", captor.getValue().getContent());
    }

    @Test
    void deleteNews() {
        newsService.delete(1L);
        verify(newsRepository).deleteById(1L);
    }

}
