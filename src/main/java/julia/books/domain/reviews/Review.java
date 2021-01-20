package julia.books.domain.reviews;

import lombok.Data;

import java.time.Instant;

@Data
public class Review {
    private Long id;
    private long bookId;
    private int readerId;
    private int rating;
    private Instant date;
    private String title;
    private String content;
}
