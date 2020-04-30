package julia.books.domain.books;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BooksService {
    private final BooksRepository repository;
    private final BookMapper bookMapper;

    SearchResult<Book> getBooks(int pageNumber, int size) {
        Page<Book> page = repository.findAll(PageRequest.of(pageNumber, size))
                .map(bookMapper::toDto);
        return new SearchResult<>(page.getContent(), page.getNumber(), page.getSize(), page.getNumberOfElements());
    }

}
