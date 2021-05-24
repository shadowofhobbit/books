package julia.books.domain.news;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
@Log4j2
@Api(tags="News")
@Validated
public class NewsController {
    private final NewsService newsService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Post news (requires ADMIN role)")
    public NewsDTO create(@RequestBody @Valid @ApiParam("Review data") NewsDTO newsDTO) {
        final Integer userId = getId();
        newsDTO.setAuthorId(userId);
        return newsService.add(newsDTO);
    }

    public Integer getId() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        final var userDetails = (UserDetailsServiceImpl.CustomUser)authentication.getPrincipal();
        final var userId = userDetails.getId();
        log.info("User id: {}", userId);
        return userId;
    }

    @GetMapping("/{newsId}")
    @ApiOperation("Get news by id")
    public ResponseEntity<NewsDTO> get(@PathVariable @ApiParam("News id") long newsId) {
        return ResponseEntity.of(newsService.getById(newsId));
    }

    @GetMapping
    @ApiOperation("Get news")
    public SearchResult<NewsDTO> getNews(@RequestParam @ApiParam("Page number") @Min(0) int page,
                                                     @RequestParam @ApiParam("Page size") @Min(0) int size) {
        return newsService.getNews(page, size);
    }

    @PutMapping("/{newsId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation("Update news")
    public NewsDTO update(@RequestBody @Valid NewsDTO newsDTO,
                         @PathVariable @ApiParam("News id") long newsId) {
        newsDTO.setId(newsId);
        return newsService.update(newsDTO);
    }

    @DeleteMapping("/{newsId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Delete news (requires ADMIN role)")
    public void delete(@PathVariable @ApiParam("Review id") long newsId) {
        newsService.delete(newsId);
    }
}
