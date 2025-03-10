package es.codeurjc.web.nitflex.dto.review;

import org.mapstruct.Mapper;

import es.codeurjc.web.nitflex.model.Review;

@Mapper(componentModel = "spring")
public interface  ReviewMapper {
    
    ReviewDTO toDTO(Review review);
    
    Review toDomain(ReviewDTO reviewDTO);

    ReviewSimpleDTO toSimpleDTO(Review review);

    Review toDomain(ReviewSimpleDTO reviewSimpleDTO);

    Review toDomain(CreateReviewRequest reviewDto);

}
