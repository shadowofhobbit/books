package julia.books.domain.books;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
@Service
public interface BookMapper {
    Book toDto(BookEntity entity);

    BookEntity toEntity(BookInvoice bookInvoice);
}
