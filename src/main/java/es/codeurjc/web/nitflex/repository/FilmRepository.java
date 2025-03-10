package es.codeurjc.web.nitflex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.codeurjc.web.nitflex.model.Film;

public interface FilmRepository extends JpaRepository<Film, Long> {


    @Query("SELECT f FROM Film f WHERE ageRating = :ageRating AND f.releaseYear BETWEEN :from AND :to")
    List<Film> findAllByRangeAndAgeRating(int from, int to, String ageRating);
}