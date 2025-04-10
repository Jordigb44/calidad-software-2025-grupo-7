package es.codeurjc.web.nitflex.unit;

import java.sql.Blob;
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

    private Film film;

    private FilmDTO filmDTO;

    private CreateFilmRequest filmRequest;

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

    private void creationMocks(Film film, FilmDTO filmDTO) {
        when(filmMapper.toDomain(any(CreateFilmRequest.class))).thenReturn(film);
        when(filmMapper.toDTO(any(Film.class))).thenReturn(filmDTO);
        when(filmRepository.save(any(Film.class))).thenReturn(film);        
        when(filmRepository.findById(film.getId())).thenReturn(Optional.of(film));
    }

    private void createAllFormatFilm(Long id, String title, String synopsis, int releaseYear, String ageRating, Blob posterFile) {

        this.filmDTO = createFilmDTO(id, title, synopsis, releaseYear, ageRating);
        this.film = createFilm(id, title, synopsis, releaseYear, ageRating);

        // Adding poster if is not null
        if (posterFile != null) {
            this.film.setPosterFile(posterFile);
        }

        this.filmRequest = createCreateFilmRequest(title, synopsis, releaseYear, ageRating);
        
        // Configure mocks
        creationMocks(film, filmDTO);
    }

    // Task 1
    @Test
    @DisplayName("Cuando se guarda una película (sin imagen) y con un título válido utilizando FilmService, se guarda en el repositorio")
    void testSaveFilmAndCheckByTitle() {

        //GIVEN
        createAllFormatFilm(1L,"El Viaje de Chihiro", "Una niña atrapada en un mundo mágico lucha por salvar a sus padres.", 2001, "+12", null);

        //WHEN
            // Save film
        FilmDTO savedFilm = filmService.save(filmRequest);
    
        //THEN
            // Verify repository interaction and result
        verify(filmRepository, times(1)).save(any(Film.class));
        when(filmRepository.findById(filmDTO.id())).thenReturn(Optional.of(film));
        assertTrue(filmRepository.findById(filmDTO.id()).isPresent());
        assertEquals("El Viaje de Chihiro", savedFilm.title());
    }

    // Task 2
    @Test
    @DisplayName("Cuando se guarda una película (sin imagen) y un título vacío utilizando FilmService, NO se guarda en el repositorio y se lanza una excepción")
    void testSaveFilmWithEmptyTitleFields() {


        //GIVEN
        createAllFormatFilm(1L,"", "Una niña atrapada en un mundo mágico lucha por salvar a sus padres.", 2001, "+12", null);
   
        //WHEN
            // Verify that exception is thrown
        assertThatThrownBy(() -> filmService.save(filmRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("The title is empty");

        //THEN        
            // Film is not in the repository (film not found)
        when(filmRepository.findById(filmDTO.id())).thenReturn(Optional.empty());
        assertFalse(filmRepository.findById(filmDTO.id()).isPresent());
    }

    // Task 3
    @Test
    @DisplayName("Cuando se borra una película que existe utilizando FilmService, se elimina del repositorio y se elimina de la lista de películas favoritas de los usuarios")
    void testDeleteFilm() {
        

        //GIVEN
        createAllFormatFilm(1L,"Viaje de Chihiro", "Una niña atrapada en un mundo mágico lucha por salvar a sus padres.", 2001, "+12", imageUtils.remoteImageToBlob("./src/main/resources/static/images/logo.png"));

            // Create users
        User user1 = new User("User1", "user1@example.com");
        user1.setId(10L);
        User user2 = new User("User2", "user2@example.com");
        user2.setId(20L); // there is no other way to set the id to the users, unless we change the code to make it more efficient
    
            // Add films to users' favorite list
        user1.getFavoriteFilms().add(film);
        user2.getFavoriteFilms().add(film);
        film.getUsersThatLiked().add(user1);
        film.getUsersThatLiked().add(user2);
    
        filmService.save(filmRequest);
        
        //WHEN
        filmService.delete(filmDTO.id());
    
            // Verify that the film has been removed from users' favorite films
        assertEquals(0, user1.getFavoriteFilms().size());
        assertEquals(0, user2.getFavoriteFilms().size());
        
    
        //THEN
            // Film is no longer in the repository (film not found)
        when(filmRepository.findById(filmDTO.id())).thenReturn(Optional.empty());
        assertTrue(filmRepository.findById(filmDTO.id()).isEmpty());
        assertEquals(0, film.getUsersThatLiked().size());
    }

    // Task 4
    @Test
    @DisplayName("Cuando se borra una película que no existe, se lanza FilmNotFoundException")
    void testDeleteNonExistentFilmThrowsException() {

        //GIVEN
        long fictionalId = 999L;
        when(filmRepository.findById(fictionalId)).thenReturn(Optional.empty());

        //WHEN
        FilmNotFoundException ex = assertThrows(FilmNotFoundException.class, () -> {
            filmService.delete(fictionalId);
        });

        //THEN
        assertEquals("Film not found with id: " + fictionalId, ex.getMessage());
        verify(filmRepository, never()).deleteById(anyLong());
    }
}
