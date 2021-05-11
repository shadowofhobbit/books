package julia.books.domain.quotes;

import julia.books.domain.accounts.AccountRepository;
import julia.books.domain.books.BooksRepository;
import julia.books.domain.books.SearchResult;
import julia.books.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.encodeBasicAuth;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class QuotesService {
    private static final int TIMEOUT = 10;
    private final QuotesMapper mapper;
    private final AccountRepository accountRepository;
    private final QuotesRepository quotesRepository;
    private final BooksRepository booksRepository;
    @Value("${books.bot.url}")
    private String BOT_BASE_URL;
    @Value("${books.bot.username}")
    private String BOT_USERNAME;
    @Value("${books.bot.password}")
    private String BOT_PASSWORD;

    public QuoteDTO add(QuoteDTO quoteDTO) {
        final var quoteEntity = mapper.toEntity(quoteDTO);
        quoteEntity.setReader(accountRepository.getOne(quoteDTO.getReaderId()));
        quoteEntity.setBook(booksRepository.getOne(quoteDTO.getBookId()));
        final var savedQuote = quotesRepository.save(quoteEntity);
        return mapper.toDto(savedQuote);
    }

    @Transactional(readOnly = true)
    public Optional<QuoteDTO> getById(long quoteId) {
        final var quoteEntity = quotesRepository.findById(quoteId);
        return quoteEntity.map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public SearchResult<QuoteDTO> getQuotesForBook(long bookId, int page, int size) {
        final var pageRequest = PageRequest.of(page, size);
        final var quotes = quotesRepository.findByBookId(bookId, pageRequest)
                .map(mapper::toDto);
        return new SearchResult<>(quotes.getContent(),
                quotes.getNumber(),
                quotes.getSize(),
                quotes.getTotalElements());
    }


    public QuoteDTO update(QuoteDTO quoteDTO) {
        final var quoteEntity = quotesRepository.findById(quoteDTO.getId()).orElseThrow();
        if (!quoteEntity.getReader().getId().equals(quoteDTO.getReaderId())) {
            throw new AccessDeniedException("Cannot update quotes added by other people");
        }
        quoteEntity.setContent(quoteDTO.getContent());
        final var savedQuote = quotesRepository.save(quoteEntity);
        return mapper.toDto(savedQuote);
    }

    public void delete(long quoteId, UserDetailsServiceImpl.CustomUser userDetails) {
        final var isAdmin = userDetails.getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> "ROLE_ADMIN".equals(grantedAuthority.getAuthority()));
        if (!isAdmin) {
            final var quote = quotesRepository.findById(quoteId).orElseThrow();
            if (!quote.getReader().getId().equals(userDetails.getId())) {
                throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
            }
        }
        quotesRepository.deleteById(quoteId);
    }

    @Transactional(readOnly = true)
    public SearchResult<QuoteDTO> getQuotesByUserId(Integer userId, int page, int size) {
        final var pageRequest = PageRequest.of(page, size);
        final var reviews = quotesRepository.findByReaderId(userId, pageRequest)
                .map(mapper::toDto);
        return new SearchResult<>(reviews.getContent(),
                reviews.getNumber(),
                reviews.getSize(),
                reviews.getTotalElements());
    }

    @Transactional(readOnly = true)
    public void publishInTelegramBot(long quoteId) {
        var quote = quotesRepository.findById(quoteId).orElseThrow();
        var httpClient = HttpClient.newHttpClient();
        var uri = URI.create(BOT_BASE_URL + "/quotes");
        var body = "{\"content\": \"" + quote.getContent() + "\", \"source\": \"" + quote.getBook().getAuthor() + "\"}";
        var request = HttpRequest.newBuilder()
                .timeout(Duration.of(TIMEOUT, ChronoUnit.SECONDS))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri)
                .headers(HttpHeaders.AUTHORIZATION, "Basic " + encodeBasicAuth(BOT_USERNAME, BOT_PASSWORD, null),
                        HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            var httpStatus = Objects.requireNonNull(HttpStatus.resolve(response.statusCode()));
            if (httpStatus.is4xxClientError()) {
                throw new IllegalArgumentException("Error publishing quote in bot");
            } else if (!httpStatus.is2xxSuccessful()) {
                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error publishing quote in bot");
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error sending quote to bot");
            e.printStackTrace();
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error sending quote to bot");
        }
    }
}
