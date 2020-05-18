package julia.books.domain.books;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class BookInvoice {
    private Long id;
    private @NotEmpty String title;
    private String author;
    private String description;
    private Integer year;
    private String language;
}
