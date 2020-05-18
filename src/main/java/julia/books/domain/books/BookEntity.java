package julia.books.domain.books;

import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Entity(name = "books")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"title", "author"}))
public class BookEntity {
    @Id
    @GeneratedValue
    private Long id;
    @NaturalId
    private String title;
    @NaturalId
    private String author;
    private String description;
    private Integer year;
    private String language;
}
