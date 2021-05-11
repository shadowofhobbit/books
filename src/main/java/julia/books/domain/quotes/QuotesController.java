package julia.books.domain.quotes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import julia.books.domain.books.SearchResult;
import julia.books.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequiredArgsConstructor
@Log4j2
@Api(tags="Quotes")
@Validated
public class QuotesController {
    private final QuotesService quotesService;

    @PostMapping("/books/{bookId}/quotes")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Create a quote (requires USER role)")
    public QuoteDTO create(@RequestBody @Valid @ApiParam("Quote data") QuoteDTO quoteDTO,
                            @PathVariable @ApiParam("Book id") long bookId) {
        quoteDTO.setBookId(bookId);
        final Integer userId = getId();
        quoteDTO.setReaderId(userId);
        return quotesService.add(quoteDTO);
    }

    public Integer getId() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        final var userDetails = (UserDetailsServiceImpl.CustomUser)authentication.getPrincipal();
        final var userId = userDetails.getId();
        log.info("User id: {}", userId);
        return userId;
    }

    @GetMapping("/quotes/{quoteId}")
    @ApiOperation("Get review by id")
    public ResponseEntity<QuoteDTO> get(@PathVariable @ApiParam("Quote id") long quoteId) {
        return ResponseEntity.of(quotesService.getById(quoteId));
    }

    @GetMapping("/books/{bookId}/quotes")
    @ApiOperation("Get quotes for book")
    public SearchResult<QuoteDTO> getQuotesForBook(@PathVariable long bookId,
                                                    @RequestParam @ApiParam("Page number") @Min(0) int page,
                                                    @RequestParam @ApiParam("Page size") @Min(0) int size) {
        return quotesService.getQuotesForBook(bookId, page, size);
    }

    @GetMapping("/accounts/{readerId}/quotes")
    @ApiOperation("Get quotes for user")
    public SearchResult<QuoteDTO> getReviewsByUser(@PathVariable Integer readerId,
                                                    @RequestParam @ApiParam(value = "Page number", required = true) int page,
                                                    @RequestParam @ApiParam(value = "Page size", required = true) int size) {
        return quotesService.getQuotesByUserId(readerId, page, size);
    }

    @PutMapping("/quotes/{quoteId}")
    @PreAuthorize("hasRole('USER') && (#this.this.getId().equals(#quoteDTO.getReaderId()))")
    @ApiOperation("Update quote")
    public QuoteDTO update(@RequestBody @Valid QuoteDTO quoteDTO,
                         @PathVariable @ApiParam("Quote id") long quoteId) {
        quoteDTO.setId(quoteId);
        return quotesService.update(quoteDTO);
    }

    @DeleteMapping("/quotes/{quoteId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Delete a quote. Users can delete their own quotes. Admins can delete any quotes.")
    public void delete(@PathVariable @ApiParam("Quote id") long quoteId, Authentication authentication) {
        final var userDetails = (UserDetailsServiceImpl.CustomUser)authentication.getPrincipal();
        quotesService.delete(quoteId, userDetails);
    }

    @PutMapping("/quotes/{quoteId}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation("Publish quote in Telegram bot")
    public void publishInTelegramBot(@PathVariable @ApiParam("Quote id") long quoteId) {
        quotesService.publishInTelegramBot(quoteId);
    }
}
