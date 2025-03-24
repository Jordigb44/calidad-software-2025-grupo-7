package es.codeurjc.web.nitflex.integration;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.rowset.serial.SerialBlob;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import es.codeurjc.web.nitflex.dto.film.FilmSimpleDTO;
import es.codeurjc.web.nitflex.model.Film;
import es.codeurjc.web.nitflex.model.User;
import es.codeurjc.web.nitflex.repository.FilmRepository;
import es.codeurjc.web.nitflex.repository.UserRepository;
import es.codeurjc.web.nitflex.service.FilmService;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Use H2 in memory
public class TestIntegration {

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilmService filmService;

    private Film film;
    private Blob posterBlob;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        // Create the film
        film = new Film();
        film.setTitle("El Viaje de Chihiro");
        film.setSynopsis("Una chica atrapada en un mundo mágico.");
        film.setReleaseYear(2001);
        film.setAgeRating("+12");

        // Create a Blob for the poster image
        File imageFile = new File("src/main/resources/static/images/no-image.png");
        try (FileInputStream fileInputStream = new FileInputStream(imageFile)) {
            byte[] imageBytes = fileInputStream.readAllBytes();
            posterBlob = new SerialBlob(imageBytes);
            film.setPosterFile(posterBlob);
        }

        // Save the film in the database
        film = filmRepository.save(film);

        // Create and save users
        user1 = new User();
        user1.setName("usuario1");
        user1.setFavoriteFilms(new ArrayList<>());
        user1.getFavoriteFilms().add(film);
        user1 = userRepository.save(user1);

        user2 = new User();
        user2.setName("usuario2");
        user2.setFavoriteFilms(new ArrayList<>());
        user2.getFavoriteFilms().add(film);
        user2 = userRepository.save(user2);
    }

    // Task 2
    @Test
    @DisplayName("Cuando se actualizan los campos 'title' y 'synopsis' de una película (SIN imagen) y con un \\r\\n" + //
                "\" + //\r\n" + //
                "\"título válido mediante FilmService, se guardan los cambios en la base de datos y se \\r\\n" + //
                "\" + //\r\n" + //
                "\"mantiene la lista de usuarios que la han marcado como favorita")
    void testUpdateFilmTitleAndSynopsisKeepsUsers() {
        // Update the film
        FilmSimpleDTO updatedFilmDTO = new FilmSimpleDTO(
            film.getId(), "Chihiro y el mundo espiritual",
            "Inolvidable aventura en un mundo mágico.",
            film.getReleaseYear(), film.getAgeRating()
        );

        filmService.update(film.getId(), updatedFilmDTO);

        // Verify the changes
        Film updatedFilm = filmRepository.findById(film.getId()).orElseThrow();

        assertEquals("Chihiro y el mundo espiritual", updatedFilm.getTitle());
        assertEquals("Inolvidable aventura en un mundo mágico.", updatedFilm.getSynopsis());
        assertEquals(2, updatedFilm.getUsersThatLiked().size());
        assertEquals(2001, updatedFilm.getReleaseYear());
        assertEquals("+12", updatedFilm.getAgeRating());
    }

    // Task 3
    @Test
    @DisplayName("Cuando se actualizan los campos 'title' y 'synopsis' de una película (CON imagen) y con un \\r\\n" + //
                "\" + //\r\n" + //
                "\"título válido mediante FilmService, se guardan los cambios en la base de datos y la \\r\\n" + //
                "\" + //\r\n" + //
                "\"imagen no cambia")
    void testUpdateFilmTitleAndSynopsisAndImageDoesntChange() throws SQLException {
        // Update the film
        FilmSimpleDTO updatedFilmDTO = new FilmSimpleDTO(
            film.getId(), "Chihiro y el mundo espiritual",
            "Inolvidable aventura en un mundo mágico.",
            film.getReleaseYear(), film.getAgeRating()
        );

        filmService.update(film.getId(), updatedFilmDTO);

        // Verify the changes
        Film updatedFilm = filmRepository.findById(film.getId()).orElseThrow();

        assertEquals("Chihiro y el mundo espiritual", updatedFilm.getTitle());
        assertEquals("Inolvidable aventura en un mundo mágico.", updatedFilm.getSynopsis());
        assertEquals(2, updatedFilm.getUsersThatLiked().size());

        // Verify that the image remains unchanged
        Blob updatedPosterBlob = updatedFilm.getPosterFile();
        assertArrayEquals(posterBlob.getBytes(1, (int) posterBlob.length()),
                          updatedPosterBlob.getBytes(1, (int) updatedPosterBlob.length()));

        assertEquals(2001, updatedFilm.getReleaseYear());
        assertEquals("+12", updatedFilm.getAgeRating());
    }
}
