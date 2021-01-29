package julia.books.domain.reviews;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import julia.books.domain.books.SearchResult;
import julia.books.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/books/{bookId}/reviews")
@RequiredArgsConstructor
@Log4j2
@Api(tags="Reviews")
public class ReviewsController {
    private final ReviewsService reviewsService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Create a review (requires USER role)")
    public ReviewDTO create(@RequestBody @Valid @ApiParam("Review data") ReviewDTO reviewDTO,
                         @PathVariable @ApiParam("Book id") long bookId,
                         Authentication authentication) {
        reviewDTO.setBookId(bookId);
        final Integer userId = getId(authentication);
        reviewDTO.setReaderId(userId);
        return reviewsService.add(reviewDTO);
    }

    private Integer getId(Authentication authentication) {
        final var userDetails = (UserDetailsServiceImpl.CustomUser)authentication.getPrincipal();
        final var userId = userDetails.getId();
        log.info("User id: {}", userId);
        return userId;
    }

    @GetMapping("/{reviewId}")
    @ApiOperation("Get review by id")
    public ReviewDTO get(@PathVariable @ApiParam("Review id") long reviewId) {
        return reviewsService.getById(reviewId);
    }

    @GetMapping
    @ApiOperation("Get reviews for book")
    public SearchResult<ReviewDTO> getAllForBook(@PathVariable long bookId,
                                              @RequestParam @ApiParam("Page number") int page,
                                              @RequestParam @ApiParam("Page size") int size) {
        return reviewsService.getReviewsForBook(bookId, page, size);
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER') && (#this.this.getId(authentication).equals(#reviewDTO.getReaderId()))")
    @ApiOperation("Update review")
    public ReviewDTO update(@RequestBody @Valid ReviewDTO reviewDTO,
                         @PathVariable @ApiParam("Book id") long bookId,
                         @PathVariable @ApiParam("Review id") long reviewId) {
        reviewDTO.setBookId(bookId);
        reviewDTO.setId(reviewId);
        return reviewsService.update(reviewDTO);
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Delete review. Users can delete their own reviews. Admins can delete any reviews.")
    public void delete(@PathVariable @ApiParam("Review id") long reviewId, Authentication authentication) {
        final var userDetails = (UserDetailsServiceImpl.CustomUser)authentication.getPrincipal();
        reviewsService.delete(reviewId, userDetails);
    }
}
