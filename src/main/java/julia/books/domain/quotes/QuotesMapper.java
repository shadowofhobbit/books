package julia.books.domain.quotes;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuotesMapper {
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "readerId", source = "reader.id")
    QuoteDTO toDto(QuoteEntity entity);
    QuoteEntity toEntity(QuoteDTO dto);
}
