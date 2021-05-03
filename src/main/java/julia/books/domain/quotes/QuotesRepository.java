package julia.books.domain.quotes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuotesRepository extends PagingAndSortingRepository<QuoteEntity, Long> {
    Page<QuoteEntity> findByBookId(Long bookId, Pageable pageable);
    Page<QuoteEntity> findByReaderId(Integer userId, Pageable pageable);
}
