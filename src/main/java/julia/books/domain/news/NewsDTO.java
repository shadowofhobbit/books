package julia.books.domain.news;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.Instant;

@Data
public class NewsDTO {
    private Long id;
    private Integer authorId;
    private Instant date;
    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
}
