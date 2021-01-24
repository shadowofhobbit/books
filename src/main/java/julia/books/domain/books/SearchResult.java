package julia.books.domain.books;

import lombok.Value;

import java.util.List;

@Value
@SuppressWarnings("PMD.DefaultPackage")
public class SearchResult<T> {
    List<T> content;
    int page;
    int size;
    int totalElements;
}
