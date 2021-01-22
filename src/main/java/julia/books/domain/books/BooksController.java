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
    @ApiOperation(value="Create book (requires USER or ADMIN role)")
    Book createBook(@Valid @RequestBody @ApiParam("Book data") BookInvoice bookInvoice) {
        return booksService.create(bookInvoice);
    }

    @GetMapping
    @ApiOperation(value="Get books")
    SearchResult<Book> getBooks(@RequestParam @ApiParam("Page number") int page, @RequestParam @ApiParam("Page size") int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be greater than or equal to 0");
        }
        if (size < 0) {
            throw new IllegalArgumentException("Size must be greater than or equal to 0");
        }
        return booksService.getBooks(page, size);
    }

    @GetMapping(path="/{id}")
    @ApiOperation(value="Get book by id")
    ResponseEntity<Book> get(@PathVariable @ApiParam("Book id") long id) {
        return ResponseEntity.of(booksService.get(id));
    }

    @PutMapping(path="/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiOperation(value="Update book (requires USER or ADMIN role)")
    void updateBook(@Valid @RequestBody @ApiParam("Book data") BookInvoice bookInvoice) {
        booksService.update(bookInvoice);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value="Delete book (requires ADMIN role)")
    void deleteBook(@PathVariable @ApiParam("Book id") long id) {
        booksService.delete(id);
    }

}
