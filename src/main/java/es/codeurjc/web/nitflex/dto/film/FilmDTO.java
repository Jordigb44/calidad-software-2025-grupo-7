package es.codeurjc.web.nitflex.dto.film;

import java.util.List;

import es.codeurjc.web.nitflex.dto.review.ReviewSimpleDTO;
import es.codeurjc.web.nitflex.dto.user.UserSimpleDTO;

public record FilmDTO(Long id, String title, String synopsis, int releaseYear, String ageRating, List<ReviewSimpleDTO> reviews, List<UserSimpleDTO> usersThatLiked) {}
