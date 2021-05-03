package julia.books.domain.quotes;

import julia.books.domain.accounts.AccountEntity;
import julia.books.domain.books.BookEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity(name = "quotes")
@Getter
@Setter
public class QuoteEntity {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private BookEntity book;
    @ManyToOne(fetch = FetchType.LAZY)
    private AccountEntity reader;
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String content;
}
