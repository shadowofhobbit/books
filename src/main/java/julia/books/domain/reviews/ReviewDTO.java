package julia.books.domain.reviews;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.time.Instant;

@Data
public class ReviewDTO {
    private Long id;
    private long bookId;
    private int reviewerId;
    @Min(1)
    @Max(10)
    private int rating;
    private Instant date;
    private String title;
    @NotEmpty
    private String content;
}
