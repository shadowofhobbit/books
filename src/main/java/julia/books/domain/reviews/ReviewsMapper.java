package julia.books.domain.reviews;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewsMapper {
    ReviewDTO toDto(ReviewEntity reviewEntity);
    ReviewEntity toEntity(ReviewDTO reviewDTO);
}
