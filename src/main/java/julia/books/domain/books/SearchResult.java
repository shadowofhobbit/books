package julia.books.domain.books;

import lombok.Value;

import java.util.List;

@Value
public class SearchResult<T> {
    List<T> content;
    int page;
    int size;
    int totalElements;
}
