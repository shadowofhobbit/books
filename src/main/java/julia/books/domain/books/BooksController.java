package julia.books.domain.books;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/books/")
public class BooksController {
    private final BooksService booksService;

    @Autowired
    public BooksController(BooksService booksService) {
        this.booksService = booksService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    Book createBook(@Valid @RequestBody BookInvoice bookInvoice) {
        return booksService.create(bookInvoice);
    }

    @GetMapping
    SearchResult<Book> getBooks(@RequestParam int page, @RequestParam int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be greater than or equal to 0");
        }
        if (size < 0) {
            throw new IllegalArgumentException("Size must be greater than or equal to 0");
        }
        return booksService.getBooks(page, size);
    }

    @GetMapping(path="{id}")
    ResponseEntity<Book> get(@PathVariable long id) {
        return ResponseEntity.of(booksService.get(id));
    }

    @PutMapping(path="{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    void updateBook(@Valid @RequestBody BookInvoice bookInvoice) {
        booksService.update(bookInvoice);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteBook(@PathVariable long id) {
        booksService.delete(id);
    }

}
