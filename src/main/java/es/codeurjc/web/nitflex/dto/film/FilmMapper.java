package es.codeurjc.web.nitflex.dto.film;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import es.codeurjc.web.nitflex.model.Film;

@Mapper(componentModel = "spring")
public interface FilmMapper {

    FilmDTO toDTO(Film film);
    
    List<FilmDTO> toDTOs(Collection<Film> films);
    
    Film toDomain(FilmDTO filmDTO);

    List<FilmDTO> toDTO(List<Film> all);

    Film toDomain(CreateFilmRequest film);

    CreateFilmRequest toCreateFilmRequest(Film film);

    FilmSimpleDTO toFilmSimpleDTO(Film updatedFilm);
    
}
