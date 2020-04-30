package julia.books.domain.books;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "books")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String author;
    private String description;
    private Integer year;
    private String language;
}
