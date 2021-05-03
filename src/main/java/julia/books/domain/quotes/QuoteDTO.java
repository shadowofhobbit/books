package julia.books.domain.quotes;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class QuoteDTO {
    private Long id;
    private Long bookId;
    private Integer readerId;
    @NotEmpty
    private String content;
}
