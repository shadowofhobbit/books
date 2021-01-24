package julia.books.domain.books;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BooksService {
    private final BooksRepository repository;
    private final BookMapper bookMapper;

    public Book create(BookInvoice bookInvoice) {
        bookInvoice.setId(null);
        final BookEntity bookEntity = bookMapper.toEntity(bookInvoice);
        final var savedEntity = repository.save(bookEntity);
        return bookMapper.toDto(savedEntity);
    }

    public SearchResult<Book> getBooks(int pageNumber, int size) {
        final Page<Book> page = repository.findAll(PageRequest.of(pageNumber, size))
                .map(bookMapper::toDto);
        return new SearchResult<>(page.getContent(), page.getNumber(), page.getSize(), page.getNumberOfElements());
    }

    public Optional<Book> get(long id) {
        return repository.findById(id).map(bookMapper::toDto);
    }

    public void update(BookInvoice bookInvoice) {
        final BookEntity bookEntity = bookMapper.toEntity(bookInvoice);
        repository.save(bookEntity);
    }

    public void delete(long bookId) {
        repository.deleteById(bookId);
    }

}
