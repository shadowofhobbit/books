package julia.books.domain.books;

import julia.books.domain.reviews.ReviewEntity;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Set;

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
    @NaturalId(mutable = true)
    private String title;
    @NaturalId(mutable = true)
    private String author;
    private String description;
    private Integer year;
    private String language;
    @OneToMany
    private Set<ReviewEntity> reviews;
}
