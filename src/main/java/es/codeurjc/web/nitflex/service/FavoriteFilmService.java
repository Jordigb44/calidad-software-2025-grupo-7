package es.codeurjc.web.nitflex.service;

import org.springframework.stereotype.Service;

import es.codeurjc.web.nitflex.dto.film.FilmDTO;
import es.codeurjc.web.nitflex.model.Film;
import es.codeurjc.web.nitflex.model.User;
import es.codeurjc.web.nitflex.repository.FilmRepository;
import es.codeurjc.web.nitflex.repository.UserRepository;

@Service
public class FavoriteFilmService {

    private UserComponent userComponent;
    private UserRepository userRepository;
    private FilmRepository filmRepository;

    public FavoriteFilmService(UserComponent userComponent, UserRepository userRepository, FilmRepository filmRepository) {
        this.userComponent = userComponent;
        this.userRepository = userRepository;
        this.filmRepository = filmRepository;
    }

    public void addToFavorites(long filmId) {
        User currentUser = userComponent.getUser();
        Film film = filmRepository.findById(filmId).orElseThrow(() -> new IllegalArgumentException("Film not found"));
        currentUser.getFavoriteFilms().add(film);
        userRepository.save(currentUser);
    }

    public void removeFromFavorites(long filmId) {
        User currentUser = userComponent.getUser();
        Film film = filmRepository.findById(filmId).orElseThrow(() -> new IllegalArgumentException("Film not found"));
        currentUser.getFavoriteFilms().remove(film);
        userRepository.save(currentUser);
    }

    public boolean isFavorite(FilmDTO film) {
        return userComponent.getUser().getFavoriteFilms().stream()
                .anyMatch(f -> f.getId().equals(film.id()));
    }
}