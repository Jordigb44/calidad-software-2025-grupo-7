package es.codeurjc.web.nitflex.dto.user;

import java.util.List;

import es.codeurjc.web.nitflex.dto.film.FilmDTO;
import es.codeurjc.web.nitflex.dto.review.ReviewDTO;

public record UserDTO(Long id, String name, String email, List<FilmDTO> favoriteFilms, List<ReviewDTO> reviews) {}