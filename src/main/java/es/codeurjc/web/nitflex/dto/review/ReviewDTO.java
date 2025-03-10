package es.codeurjc.web.nitflex.dto.review;

import java.util.Date;

import es.codeurjc.web.nitflex.dto.film.FilmDTO;
import es.codeurjc.web.nitflex.dto.user.UserDTO;

public record ReviewDTO(Long id, String text, int score, Date created_at, UserDTO user, FilmDTO film) {}
