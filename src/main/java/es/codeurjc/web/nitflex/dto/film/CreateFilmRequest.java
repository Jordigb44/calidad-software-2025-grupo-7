package es.codeurjc.web.nitflex.dto.film;

public record CreateFilmRequest(String title, String synopsis, int releaseYear, String ageRating) {}