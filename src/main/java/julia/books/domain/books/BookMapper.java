package julia.books.domain.books;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
@Service
public interface BookMapper {
    BookDTO toDto(BookEntity entity);

    BookEntity toEntity(BookDTO bookDTO);
}
