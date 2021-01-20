package julia.books.domain.reviews;

import julia.books.domain.books.SearchResult;
import julia.books.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/books/{bookId}/reviews")
@RequiredArgsConstructor
@Log4j2
public class ReviewsController {
    private final ReviewsService reviewsService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public Review create(@RequestBody @Valid ReviewInvoice reviewInvoice,
                         @PathVariable long bookId,
                         Authentication authentication) {
        reviewInvoice.setBookId(bookId);
        Integer userId = getId(authentication);
        reviewInvoice.setReaderId(userId);
        return reviewsService.add(reviewInvoice);
    }

    private Integer getId(Authentication authentication) {
        var userDetails = (UserDetailsServiceImpl.CustomUser)authentication.getPrincipal();
        var userId = userDetails.getId();
        log.info("User id: {}", userId);
        return userId;
    }

    @GetMapping("/{reviewId}")
    public Review get(@PathVariable long reviewId) {
        return reviewsService.getById(reviewId);
    }

    @GetMapping
    public SearchResult<Review> getAllForBook(@PathVariable long bookId,
                                              @RequestParam int page,
                                              @RequestParam int size) {
        return reviewsService.getReviewsForBook(bookId, page, size);
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER') && (#this.this.getId(authentication).equals(#reviewInvoice.getReaderId()))")
    public Review update(@RequestBody @Valid ReviewInvoice reviewInvoice,
                         @PathVariable long bookId,
                         @PathVariable long reviewId) {
        reviewInvoice.setBookId(bookId);
        reviewInvoice.setId(reviewId);
        return reviewsService.update(reviewInvoice);
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void delete(@PathVariable long reviewId, Authentication authentication) {
        var userDetails = (UserDetailsServiceImpl.CustomUser)authentication.getPrincipal();
        reviewsService.delete(reviewId, userDetails);
    }
}
