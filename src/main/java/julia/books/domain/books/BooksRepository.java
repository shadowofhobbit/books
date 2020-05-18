package julia.books.domain.books;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BooksRepository extends PagingAndSortingRepository<BookEntity, Long> {
}
