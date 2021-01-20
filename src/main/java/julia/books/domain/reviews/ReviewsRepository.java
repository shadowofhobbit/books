package julia.books.domain.reviews;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewsRepository extends JpaRepository<ReviewEntity, Long> {

    Page<ReviewEntity> findByBookId(Long bookId, Pageable pageable);
}
