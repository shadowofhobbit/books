package julia.books.domain.reviews;

import julia.books.domain.accounts.AccountEntity;
import julia.books.domain.books.BookEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.time.Instant;

@Entity(name="reviews")
@Getter
@Setter
public class ReviewEntity {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private BookEntity book;
    @ManyToOne(fetch = FetchType.LAZY)
    private AccountEntity reviewer;
    @Min(1)
    @Max(10)
    private int rating;
    private Instant date;
    private String title;
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String content;
}
