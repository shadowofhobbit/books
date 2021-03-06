package julia.books.domain.reviews;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewsMapper {
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "reviewerId", source = "reviewer.id")
    ReviewDTO toDto(ReviewEntity reviewEntity);
    ReviewEntity toEntity(ReviewDTO reviewDTO);
}
