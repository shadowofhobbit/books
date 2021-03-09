package julia.books.domain.reviews;

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

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewsService {
    private final ReviewsMapper mapper;
    private final AccountRepository accountRepository;
    private final ReviewsRepository reviewsRepository;
    private final BooksRepository booksRepository;

    public ReviewDTO add(ReviewDTO reviewDTO) {
        reviewDTO.setDate(Instant.now());
        final var reviewEntity = mapper.toEntity(reviewDTO);
        reviewEntity.setReviewer(accountRepository.getOne(reviewDTO.getReviewerId()));
        reviewEntity.setBook(booksRepository.getOne(reviewDTO.getBookId()));
        final var savedReview = reviewsRepository.save(reviewEntity);
        return mapper.toDto(savedReview);
    }

    @Transactional(readOnly = true)
    public Optional<ReviewDTO> getById(long reviewId) {
        final var reviewEntity = reviewsRepository.findById(reviewId);
        return reviewEntity.map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public SearchResult<ReviewDTO> getReviewsForBook(long bookId, int page, int size) {
        final var pageRequest = PageRequest.of(page, size);
        final var reviews = reviewsRepository.findByBookId(bookId, pageRequest)
                .map(mapper::toDto);
        return new SearchResult<>(reviews.getContent(),
                reviews.getNumber(),
                reviews.getSize(),
                reviews.getTotalElements());
    }


    public ReviewDTO update(ReviewDTO reviewDTO) {
        final var reviewEntity = reviewsRepository.findById(reviewDTO.getId()).orElseThrow();
        if (reviewEntity.getReviewer().getId() != reviewDTO.getReviewerId()) {
            throw new AccessDeniedException("Cannot update reviews by other people");
        }
        reviewEntity.setRating(reviewDTO.getRating());
        reviewEntity.setTitle(reviewDTO.getTitle());
        reviewEntity.setContent(reviewDTO.getContent());
        final var savedReview = reviewsRepository.save(reviewEntity);
        return mapper.toDto(savedReview);
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
                .map(mapper::toDto);
        return new SearchResult<>(reviews.getContent(),
                reviews.getNumber(),
                reviews.getSize(),
                reviews.getTotalElements());
    }
}
