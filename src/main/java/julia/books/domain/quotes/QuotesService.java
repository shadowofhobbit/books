package julia.books.domain.quotes;

import julia.books.domain.accounts.AccountRepository;
import julia.books.domain.books.BooksRepository;
import julia.books.domain.books.SearchResult;
import julia.books.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuotesService {
    private final QuotesMapper mapper;
    private final AccountRepository accountRepository;
    private final QuotesRepository quotesRepository;
    private final BooksRepository booksRepository;

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
}
