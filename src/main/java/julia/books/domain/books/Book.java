package julia.books.domain.books;

import lombok.Data;

@Data
public class Book {
    private Long id;
    private String title;
    private String author;
    private String description;
    private Integer year;
    private String language;
}
