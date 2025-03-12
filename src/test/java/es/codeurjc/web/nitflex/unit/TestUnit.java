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

    @Test
    @DisplayName ("Cuando se guarda una película (sin imagen) y con un título válido utilizando FilmService,\r\n se guarda en el repositorio")
    void testSaveFilmAndCheckByTitle() {
        CreateFilmRequest filmRequest = new CreateFilmRequest(
            "El Viaje de Chihiro", 
            "Una niña atrapada en un mundo mágico lucha por salvar a sus padres.", 
            2001, 
            "+12"
        );  
        
        Film film = new Film();
        
        FilmDTO filmDTO = new FilmDTO(
            1L,
            "El Viaje de Chihiro", 
            "Una niña atrapada en un mundo mágico lucha por salvar a sus padres.", 
            2001, 
            "+12", 
            Collections.emptyList(), // reviews
            Collections.emptyList() // users
        );

        // Configure mocks
        when(filmMapper.toDomain(any(CreateFilmRequest.class))).thenReturn(film);
        when(filmMapper.toDTO(any(Film.class))).thenReturn(filmDTO);
        when(filmRepository.save(any(Film.class))).thenReturn(film); 

        FilmDTO savedFilm = filmService.save(filmRequest);  

        verify(filmRepository, times(1)).save(any(Film.class));  
        assertNotNull(savedFilm);  
        assertEquals("El Viaje de Chihiro", savedFilm.title()); 


        // para el test de integración, como se usa la bbdd, lo suyo es comprobar con todos los campos que se ha guardado bien
    }

    @Test
    @DisplayName ("Cuando se guarda una película (sin imagen) y un título vacío utilizando FilmService,\r\n" + //
                "NO se guarda en el repositorio y se lanza una excepción")
    void testSaveFilmWithEmptyTitleFields() {
        // Arrange
        CreateFilmRequest filmRequest = new CreateFilmRequest(
            "",
            "Una niña atrapada en un mundo mágico lucha por salvar a sus padres.", 
            2001,
            ""
        ); // Interfaz de agregar Pelicula, nos devueklve un objeto Pelicula
        
        Film film = new Film();
        
        FilmDTO filmDTO = new FilmDTO(
            1L,
            "", 
            "Una niña atrapada en un mundo mágico lucha por salvar a sus padres.", 
            2001, 
            "", 
            Collections.emptyList(), // reviews
            Collections.emptyList() // usuarios
        ); // Interfaz del la pelicula con reviws y usuarios, nos devuelve un objeto PeliculaDTO

        // Configurar mocks
        when(filmMapper.toDomain(any(CreateFilmRequest.class))).thenReturn(film);
        when(filmMapper.toDTO(any(Film.class))).thenReturn(filmDTO);
        when(filmRepository.save(any(Film.class))).thenReturn(film);

        // Act
        assertThatThrownBy(() -> filmService.save(filmRequest))
            .isInstanceOf(IllegalArgumentException.class) // Cambia según la excepción real
            .hasMessageContaining("The title is empty"); // Ajusta el mensaje esperado
        // Assert
        verify(filmRepository, times(0)).save(any(Film.class)); // comprobamos que no se llama el metodo save
        // Next asserts are optional, but we're going to assure the film has been saved
        assertEquals("Una niña atrapada en un mundo mágico lucha por salvar a sus padres.", savedFilm.synopsis());
        assertEquals(2001, savedFilm.releaseYear());
        assertEquals("+12", savedFilm.ageRating());
        assertTrue(savedFilm.reviews().isEmpty(), "La lista de reviews debería estar vacía");
        assertTrue(savedFilm.usersThatLiked().isEmpty(), "La lista de usuarios debería estar vacía");       
    }
}
