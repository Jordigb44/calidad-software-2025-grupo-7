package es.codeurjc.web.nitflex.unit;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import es.codeurjc.web.nitflex.dto.film.CreateFilmRequest;
import es.codeurjc.web.nitflex.dto.film.FilmDTO;
import es.codeurjc.web.nitflex.dto.film.FilmMapper;
import es.codeurjc.web.nitflex.model.Film;
import es.codeurjc.web.nitflex.model.User;
import es.codeurjc.web.nitflex.repository.FilmRepository;
import es.codeurjc.web.nitflex.repository.UserRepository;
import es.codeurjc.web.nitflex.service.FilmService;
import es.codeurjc.web.nitflex.service.exceptions.FilmNotFoundException;
import es.codeurjc.web.nitflex.utils.ImageUtils;

public class TestUnit {
    @Mock
    private FilmRepository filmRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FilmMapper filmMapper;

    @Mock
    private ImageUtils imageUtils;

    private FilmService filmService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filmService = new FilmService(filmRepository, userRepository, imageUtils, filmMapper);
    }

    private FilmDTO createFilmDTO(Long id, String title, String synopsis, int releaseYear, String ageRating) {
        return new FilmDTO(id, title, synopsis, releaseYear, ageRating, Collections.emptyList(), Collections.emptyList());
    }

    private Film createFilm(Long id, String title, String synopsis, int releaseYear, String ageRating) {
        Film film = new Film();
        film.setId(id);
        film.setTitle(title);
        film.setSynopsis(synopsis);
        film.setReleaseYear(releaseYear);
        film.setAgeRating(ageRating);
        return film;
    }

    private CreateFilmRequest createCreateFilmRequest(String title, String synopsis, int releaseYear, String ageRating) {
        return new CreateFilmRequest(title, synopsis, releaseYear, ageRating);
    }

    // Verifying repository save method
    private void verifySave() {
        verify(filmRepository, times(1)).save(any(Film.class));
    }

    private void creationMocks(Film film, FilmDTO filmDTO) {
        when(filmMapper.toDomain(any(CreateFilmRequest.class))).thenReturn(film);
        when(filmMapper.toDTO(any(Film.class))).thenReturn(filmDTO);
        when(filmRepository.save(any(Film.class))).thenReturn(film);        
        when(filmRepository.findById(film.getId())).thenReturn(Optional.of(film));
    }

    // Task 1
    @Test
    @DisplayName("Cuando se guarda una película (sin imagen) y con un título válido utilizando FilmService, se guarda en el repositorio")
    void testSaveFilmAndCheckByTitle() {
        // New film
        CreateFilmRequest filmRequest = createCreateFilmRequest("El Viaje de Chihiro", 
                "Una niña atrapada en un mundo mágico lucha por salvar a sus padres.", 2001, "+12");
        FilmDTO filmDTO = createFilmDTO(1L, "El Viaje de Chihiro", 
                "Una niña atrapada en un mundo mágico lucha por salvar a sus padres.", 2001, "+12");
        // Its neccesary for saving the film
        Film film = createFilm(1L, "Viaje de Chihiro", 
                "Una niña atrapada en un mundo mágico lucha por salvar a sus padres.", 2001, "+12");

        // Configure mocks
        creationMocks(film, filmDTO);

        // Save film
        FilmDTO savedFilm = filmService.save(filmRequest);

        // Verify repository interaction and result
        verifySave();
        when(filmRepository.findById(filmDTO.id())).thenReturn(Optional.of(film));
        assertTrue(filmRepository.findById(filmDTO.id()).isPresent());
        assertEquals("El Viaje de Chihiro", savedFilm.title());
    }

    // Task 2
    @Test
    @DisplayName("Cuando se guarda una película (sin imagen) y un título vacío utilizando FilmService, NO se guarda en el repositorio y se lanza una excepción")
    void testSaveFilmWithEmptyTitleFields() {
        // Create request and DTO
        CreateFilmRequest filmRequest = createCreateFilmRequest("", 
                "Una niña atrapada en un mundo mágico lucha por salvar a sus padres.", 2001, "");
        FilmDTO filmDTO = createFilmDTO(1L, "", 
                "Una niña atrapada en un mundo mágico lucha por salvar a sus padres.", 2001, "");
        // Neccessary for assertFalse
        Film film = createFilm(1L, "Viaje de Chihiro", 
                "Una niña atrapada en un mundo mágico lucha por salvar a sus padres.", 2001, "+12");

        creationMocks(film, filmDTO);

        // Verify that exception is thrown
        assertThatThrownBy(() -> filmService.save(filmRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("The title is empty");

        // Film is not in the repository (film not found)
        when(filmRepository.findById(filmDTO.id())).thenReturn(Optional.empty());
        assertFalse(filmRepository.findById(filmDTO.id()).isPresent());
    }

    // Task 3
    @Test
    @DisplayName("Cuando se borra una película que existe utilizando FilmService, se elimina del repositorio y se elimina de la lista de películas favoritas de los usuarios")
    void testDeleteFilm() {
        Long filmId = 1L;
        // Create Request for saving the film
        CreateFilmRequest filmRequest = createCreateFilmRequest("Viaje de Chihiro", 
                "Una niña atrapada en un mundo mágico lucha por salvar a sus padres.", 2001, "");
        // Create FilmDTO and Film object
        FilmDTO filmDTO = createFilmDTO(filmId, "Viaje de Chihiro", 
                "Una niña atrapada en un mundo mágico lucha por salvar a sus padres.", 2001, "+12");
        // Its neccesary for adding favorite films to users list (the aplication requires Film not FilmDTO)
        Film film = createFilm(filmId, "Viaje de Chihiro", 
                "Una niña atrapada en un mundo mágico lucha por salvar a sus padres.", 2001, "+12");

        creationMocks(film, filmDTO);
    
        // Create users
        User user1 = new User("User1", "user1@example.com");
        user1.setId(10L); // there is no other way to set the id to the users, unless we change the code to make it more efficient
        User user2 = new User("User2", "user2@example.com");
        user2.setId(20L); // there is no other way to set the id to the users, unless we change the code to make it more efficient
    
        // Add films to users' favorite list
        user1.getFavoriteFilms().add(film);
        user2.getFavoriteFilms().add(film);
        film.getUsersThatLiked().add(user1);
        film.getUsersThatLiked().add(user2);
    
        filmService.save(filmRequest);

        // Act: delete the film
        filmService.delete(filmDTO.id());
    
        // Verify that the film has been removed from users' favorite films
        assertEquals(0, user1.getFavoriteFilms().size());
        assertEquals(0, user2.getFavoriteFilms().size());
    
        // Film is no longer in the repository (film not found)
        when(filmRepository.findById(filmDTO.id())).thenReturn(Optional.empty());
        assertTrue(filmRepository.findById(filmDTO.id()).isEmpty());
    }

    // Task 4
    @Test
    @DisplayName("Cuando se borra una película que no existe, se lanza FilmNotFoundException")
    void testDeleteNonExistentFilmThrowsException() {
        long fictionalId = 999L;

        when(filmRepository.findById(fictionalId)).thenReturn(Optional.empty());

        FilmNotFoundException ex = assertThrows(FilmNotFoundException.class, () -> {
            filmService.delete(fictionalId);
        });

        assertEquals("Film not found with id: " + fictionalId, ex.getMessage());
        verify(filmRepository, never()).deleteById(anyLong());
    }
}
