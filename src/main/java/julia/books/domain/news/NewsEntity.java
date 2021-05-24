package julia.books.domain.news;

import julia.books.domain.accounts.AccountEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity(name="news")
@Getter
@Setter
public class NewsEntity {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private AccountEntity author;
    @NotNull
    private Instant date;
    @NotEmpty
    private String title;
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String content;
}
