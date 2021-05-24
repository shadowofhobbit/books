package julia.books.domain.news;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    @Mapping(target = "authorId", source = "author.id")
    NewsDTO toDto(NewsEntity newsEntity);

    @Mapping(target = "author", ignore = true)
    NewsEntity toEntity(NewsDTO newsDTO);
}
