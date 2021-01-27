package julia.books.domain.books;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/books")
@Api(tags = "Books")
public class BooksController {
    private final BooksService booksService;

    @Autowired
    public BooksController(BooksService booksService) {
        this.booksService = booksService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Create book (requires USER or ADMIN role)")
    public BookDTO createBook(@Valid @RequestBody @ApiParam("Book data") BookDTO bookDTO) {
        return booksService.create(bookDTO);
    }

    @GetMapping
    @ApiOperation("Get books")
    public SearchResult<BookDTO> getBooks(@RequestParam @ApiParam("Page number") int page, @RequestParam @ApiParam("Page size") int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be greater than or equal to 0");
        }
        if (size < 0) {
            throw new IllegalArgumentException("Size must be greater than or equal to 0");
        }
        return booksService.getBooks(page, size);
    }

    @GetMapping(path="/{id}")
    @ApiOperation("Get book by id")
    public ResponseEntity<BookDTO> get(@PathVariable @ApiParam("Book id") long id) {
        return ResponseEntity.of(booksService.get(id));
    }

    @PutMapping(path="/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiOperation("Update book (requires USER or ADMIN role)")
    public void updateBook(@PathVariable long id, @Valid @RequestBody @ApiParam("Book data") BookDTO book) {
        book.setId(id);
        booksService.update(book);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Delete book (requires ADMIN role)")
    public void deleteBook(@PathVariable @ApiParam("Book id") long id) {
        booksService.delete(id);
    }

}
