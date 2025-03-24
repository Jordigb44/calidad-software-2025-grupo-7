package es.codeurjc.web.nitflex.unit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import es.codeurjc.web.nitflex.dto.film.CreateFilmRequest;
import es.codeurjc.web.nitflex.dto.film.FilmDTO;
import es.codeurjc.web.nitflex.dto.film.FilmMapper;
import es.codeurjc.web.nitflex.model.Film;
import es.codeurjc.web.nitflex.repository.FilmRepository;
import es.codeurjc.web.nitflex.repository.UserRepository;
import es.codeurjc.web.nitflex.service.FilmService;
import es.codeurjc.web.nitflex.utils.ImageUtils;
public class TestUnit {
    // Here we set up the simulations
    @Mock
    private FilmRepository filmRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FilmMapper filmMapper;

    @Mock
    private ImageUtils imageUtils;

    private FilmService filmService;

    // Here we initialize the simulations
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filmService = new FilmService(filmRepository, userRepository, imageUtils, filmMapper);
    }

    // Task 1
    @Test
    @DisplayName ("Cuando se guarda una película (sin imagen) y con un título válido utilizando FilmService,\r\n se guarda en el repositorio")
    void testSaveFilmAndCheckByTitle() {
        // Create films
        CreateFilmRequest filmRequest = new CreateFilmRequest(
            "El Viaje de Chihiro",
            "Una niña atrapada en un mundo mágico lucha por salvar a sus padres.",
            2001,
            "+12"
        ); // Interface to add film, returns film object
        
        Film film = new Film();
        
        FilmDTO filmDTO = new FilmDTO(
            1L,
            "El Viaje de Chihiro",
            "Una niña atrapada en un mundo mágico lucha por salvar a sus padres.",
            2001,
            "+12",
            Collections.emptyList(), // reviews
            Collections.emptyList() // users
        );  // Interface of the film with users and reviews, returns objet FilmDTO

        // Configure mocks
        creationMocks(film, filmDTO);

        // Save film
        FilmDTO savedFilm = filmService.save(filmRequest);

        // Verify that the film was succesfully added
        verify(filmRepository, times(1)).save(any(Film.class));
        assertNotNull(savedFilm);
        assertEquals("El Viaje de Chihiro", savedFilm.title());
    }

    // Task 2
    @Test
    @DisplayName ("Cuando se guarda una película (sin imagen) y un título vacío utilizando FilmService,\r\n" + //
                "NO se guarda en el repositorio y se lanza una excepción")
    void testSaveFilmWithEmptyTitleFields() {
        // Create films
        CreateFilmRequest filmRequest = new CreateFilmRequest(
            "",
            "Una niña atrapada en un mundo mágico lucha por salvar a sus padres.",
            2001,
            ""
        ); // Interface to add film, returns film object
        
        Film film = new Film();
        
        FilmDTO filmDTO = new FilmDTO(
            1L,
            "",
            "Una niña atrapada en un mundo mágico lucha por salvar a sus padres.",
            2001,
            "",
            Collections.emptyList(), // reviews
            Collections.emptyList() // users
        ); // Interface of the film with users and reviews, returns objet FilmDTO

        creationMocks(film, filmDTO);

        // Verify that film film was not save and throws exception
        assertThatThrownBy(() -> filmService.save(filmRequest))
            .isInstanceOf(IllegalArgumentException.class) // Changes depending of the exception
            .hasMessageContaining("The title is empty"); // Adjust espected message
        
        verify(filmRepository, times(0)).save(any(Film.class)); // check save method is not called
    }

    private void creationMocks(Film film, FilmDTO filmDTO) {
        when(filmMapper.toDomain(any(CreateFilmRequest.class))).thenReturn(film);
        when(filmMapper.toDTO(any(Film.class))).thenReturn(filmDTO);
        when(filmRepository.save(any(Film.class))).thenReturn(film);
    }
}
