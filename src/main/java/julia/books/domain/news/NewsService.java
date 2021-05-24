package julia.books.domain.news;

import julia.books.domain.accounts.AccountRepository;
import julia.books.domain.books.SearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsService {
    private final NewsMapper mapper;
    private final NewsRepository newsRepository;
    private final AccountRepository accountRepository;

    public NewsDTO add(NewsDTO newsDTO) {
        newsDTO.setDate(Instant.now());
        final var newsEntity = mapper.toEntity(newsDTO);
        newsEntity.setAuthor(accountRepository.getOne(newsDTO.getAuthorId()));
        final var savedNews = newsRepository.save(newsEntity);
        return mapper.toDto(savedNews);
    }

    @Transactional(readOnly = true)
    public Optional<NewsDTO> getById(long newsId) {
        final var newsEntity = newsRepository.findById(newsId);
        return newsEntity.map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public SearchResult<NewsDTO> getNews(int page, int size) {
        final var pageRequest = PageRequest.of(page, size);
        final var reviews = newsRepository.findAll(pageRequest)
                .map(mapper::toDto);
        return new SearchResult<>(reviews.getContent(),
                reviews.getNumber(),
                reviews.getSize(),
                reviews.getTotalElements());
    }


    public NewsDTO update(NewsDTO newsDTO) {
        final var newsEntity = newsRepository.findById(newsDTO.getId()).orElseThrow();
        newsEntity.setTitle(newsDTO.getTitle());
        newsEntity.setContent(newsDTO.getContent());
        final var savedNews = newsRepository.save(newsEntity);
        return mapper.toDto(savedNews);
    }

    public void delete(long newsId) {
        newsRepository.deleteById(newsId);
    }
}
