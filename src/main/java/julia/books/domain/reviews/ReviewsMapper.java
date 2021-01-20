package julia.books.domain.reviews;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewsMapper {
    Review toDto(ReviewEntity reviewEntity);
    ReviewEntity toEntity(ReviewInvoice reviewInvoice);
}
