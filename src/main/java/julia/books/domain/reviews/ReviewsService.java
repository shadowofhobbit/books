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

    public ReviewDTO add(ReviewDTO reviewDTO) {
        final var reviewEntity = mapper.toEntity(reviewDTO);
        reviewEntity.setReviewer(accountRepository.getOne(reviewDTO.getReaderId()));
        reviewEntity.setBook(booksRepository.getOne(reviewDTO.getBookId()));
        final var savedReview = reviewsRepository.save(reviewEntity);
        final var review = mapper.toDto(savedReview);
        review.setReaderId(reviewEntity.getReviewer().getId());
        review.setBookId(reviewEntity.getBook().getId());
        return review;
    }

    @Transactional(readOnly = true)
    public ReviewDTO getById(long reviewId) {
        final var reviewEntity = reviewsRepository.findById(reviewId).orElseThrow();
        final var review = mapper.toDto(reviewEntity);
        review.setReaderId(reviewEntity.getReviewer().getId());
        review.setBookId(reviewEntity.getBook().getId());
        return review;
    }

    @Transactional(readOnly = true)
    public SearchResult<ReviewDTO> getReviewsForBook(long bookId, int page, int size) {
        final var pageRequest = PageRequest.of(page, size);
        final var reviews = reviewsRepository.findByBookId(bookId, pageRequest)
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


    public ReviewDTO update(ReviewDTO reviewDTO) {
        final var reviewEntity = reviewsRepository.findById(reviewDTO.getId()).orElseThrow();
        reviewEntity.setRating(reviewDTO.getRating());
        reviewEntity.setTitle(reviewDTO.getTitle());
        reviewEntity.setContent(reviewDTO.getContent());
        final var savedReview = reviewsRepository.save(reviewEntity);
        final var review = mapper.toDto(savedReview);
        review.setReaderId(savedReview.getReviewer().getId());
        review.setBookId(savedReview.getBook().getId());
        return review;
    }

    public void delete(long reviewId, UserDetailsServiceImpl.CustomUser userDetails) {
        final var isAdmin = userDetails.getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> "ROLE_ADMIN".equals(grantedAuthority.getAuthority()));
        if (!isAdmin) {
            final var review = reviewsRepository.findById(reviewId).orElseThrow();
            if (!review.getReviewer().getId().equals(userDetails.getId())) {
                throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
            }
        }
        reviewsRepository.deleteById(reviewId);
    }

    @Transactional(readOnly = true)
    public SearchResult<ReviewDTO> getReviewsByUserId(Integer userId, int page, int size) {
        final var pageRequest = PageRequest.of(page, size);
        final var reviews = reviewsRepository.findByReviewerId(userId, pageRequest)
                .map(reviewEntity -> {
                    final var review = mapper.toDto(reviewEntity);
                    review.setReaderId(reviewEntity.getReviewer().getId());
                    review.setBookId(reviewEntity.getBook().getId());
                    return review;
                });
        return new SearchResult<>(reviews.getContent(),
                reviews.getNumber(),
                reviews.getSize(),
                reviews.getTotalElements());
    }
}
