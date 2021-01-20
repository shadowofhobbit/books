package julia.books.domain.reviews;

import julia.books.domain.accounts.AccountRepository;
import julia.books.domain.books.BooksRepository;
import julia.books.domain.books.SearchResult;
import julia.books.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewsService {
    private final ReviewsMapper mapper;
    private final AccountRepository accountRepository;
    private final ReviewsRepository reviewsRepository;
    private final BooksRepository booksRepository;

    public Review add(ReviewInvoice reviewInvoice) {
        var reviewEntity = mapper.toEntity(reviewInvoice);
        reviewEntity.setReviewer(accountRepository.getOne(reviewInvoice.getReaderId()));
        reviewEntity.setBook(booksRepository.getOne(reviewInvoice.getBookId()));
        var savedReview = reviewsRepository.save(reviewEntity);
        var review = mapper.toDto(savedReview);
        review.setReaderId(reviewEntity.getReviewer().getId());
        review.setBookId(reviewEntity.getBook().getId());
        return review;
    }

    @Transactional(readOnly = true)
    public Review getById(long reviewId) {
        var reviewEntity = reviewsRepository.findById(reviewId).orElseThrow();
        var review = mapper.toDto(reviewEntity);
        review.setReaderId(reviewEntity.getReviewer().getId());
        review.setBookId(reviewEntity.getBook().getId());
        return review;
    }

    @Transactional(readOnly = true)
    public SearchResult<Review> getReviewsForBook(long bookId, int page, int size) {
        var pageRequest = PageRequest.of(page, size);
        var reviews = reviewsRepository.findByBookId(bookId, pageRequest)
                .map(reviewEntity -> {
                    var review = mapper.toDto(reviewEntity);
                    review.setReaderId(reviewEntity.getReviewer().getId());
                    review.setBookId(reviewEntity.getBook().getId());
                    return review;
                });
        return new SearchResult<>(reviews.getContent(),
                reviews.getNumber(),
                reviews.getSize(),
                reviews.getTotalElements());
    }


    public Review update(ReviewInvoice reviewInvoice) {
        var reviewEntity = reviewsRepository.findById(reviewInvoice.getId()).orElseThrow();
        reviewEntity.setRating(reviewInvoice.getRating());
        reviewEntity.setTitle(reviewInvoice.getTitle());
        reviewEntity.setContent(reviewInvoice.getContent());
        var savedReview = reviewsRepository.save(reviewEntity);
        var review = mapper.toDto(savedReview);
        review.setReaderId(savedReview.getReviewer().getId());
        review.setBookId(savedReview.getBook().getId());
        return review;
    }

    public void delete(long reviewId, UserDetailsServiceImpl.CustomUser userDetails) {
        var isAdmin = userDetails.getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            var review = reviewsRepository.findById(reviewId).orElseThrow();
            if (!review.getReviewer().getId().equals(userDetails.getId())) {
                throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
            }
        }
        reviewsRepository.deleteById(reviewId);
    }
}
