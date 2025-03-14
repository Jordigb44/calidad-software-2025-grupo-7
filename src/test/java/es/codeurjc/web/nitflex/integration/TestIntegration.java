package es.codeurjc.web.nitflex.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.rowset.serial.SerialBlob;

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

    @Test
    @DisplayName ("Cuando se actualizan los campos 'title' y 'synopsis' de una película (SIN imagen) y con un \r\n" + //
                    "título válido mediante FilmService, se guardan los cambios en la base de datos y se \r\n" + //
                    "mantiene la lista de usuarios que la han marcado como favorita")
    void testUpdateFilmTitleAndSynopsisKeepsUsers() {
        // Save the film in DataBase
        Film film = new Film();
        film.setTitle("El Viaje de Chihiro");
        film.setSynopsis("Una niña atrapada en un mundo mágico.");
        film.setReleaseYear(2001);
        film.setAgeRating("+12");
        film = filmRepository.save(film);

        // Create and save users
        User user1 = new User();
        user1.setName("usuario1");
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setName("usuario2");
        user2 = userRepository.save(user2);

        // Assign favorite film to users
        if (user1.getFavoriteFilms() == null) {
            user1.setFavoriteFilms(new ArrayList<>()); // Initialize the list if its null
        }
        user1.getFavoriteFilms().add(film);

        if (user2.getFavoriteFilms() == null) {
            user2.setFavoriteFilms(new ArrayList<>());
        }
        user2.getFavoriteFilms().add(film);

        userRepository.save(user1);
        userRepository.save(user2);

        // Update the film
        FilmSimpleDTO updatedFilmDTO = new FilmSimpleDTO(
            film.getId(), "Chihiro y el mundo espiritual",
            "Una aventura inolvidable en un mundo mágico.",
            film.getReleaseYear(), film.getAgeRating()
        );

        filmService.update(film.getId(), updatedFilmDTO);

        // Verify the changes have been saved
        Film updatedFilm = filmRepository.findById(film.getId()).orElseThrow();

        assertEquals("Chihiro y el mundo espiritual", updatedFilm.getTitle());
        assertEquals("Una aventura inolvidable en un mundo mágico.", updatedFilm.getSynopsis());
        assertEquals(2, updatedFilm.getUsersThatLiked().size());

        // Optional assertions to verify that the film was saved correctly
        assertEquals(2001, updatedFilm.getReleaseYear());
        assertEquals("+12", updatedFilm.getAgeRating());
    }

    @Test
    @DisplayName ("Cuando se actualizan los campos 'title' y 'synopsis' de una película (CON imagen) y con un \r\n" + //
                    "título válido mediante FilmService, se guardan los cambios en la base de datos y la \r\n" + //
                    "imagen no cambia")
    void testUpdateFilmTitleAndSynopsisAndImageDoesntChange() throws SQLException {
        // Create the film
        Film film = new Film();
        film.setTitle("El Viaje de Chihiro");
        film.setSynopsis("Una niña atrapada en un mundo mágico.");
        film.setReleaseYear(2001);
        film.setAgeRating("+12");

        // Create Blob Poster Image
        File imageFile = new File("src/main/resources/static/images/no-image.png");
        byte[] imageBytes = null;
        Blob posterBlob = null;

        // Use try-with-resources to ensure the stream is closed
        try (FileInputStream fileInputStream = new FileInputStream(imageFile)) {
            imageBytes = fileInputStream.readAllBytes();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create the Blob
        if (imageBytes != null) {
            try {
                posterBlob = new SerialBlob(imageBytes);
                film.setPosterFile(posterBlob);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Save the film in DataBase
        film = filmRepository.save(film);

        // Create and save users
        User user1 = new User();
        user1.setName("usuario1");
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setName("usuario2");
        user2 = userRepository.save(user2);

        // Assign favorite film to users
        if (user1.getFavoriteFilms() == null) {
            user1.setFavoriteFilms(new ArrayList<>()); // Initialize the list if its null
        }
        user1.getFavoriteFilms().add(film);

        if (user2.getFavoriteFilms() == null) {
            user2.setFavoriteFilms(new ArrayList<>());
        }
        user2.getFavoriteFilms().add(film);

        userRepository.save(user1);
        userRepository.save(user2);

        // Update the film title & synopsis
        FilmSimpleDTO updatedFilmDTO = new FilmSimpleDTO(
            film.getId(), "Chihiro y el mundo espiritual",
            "Una aventura inolvidable en un mundo mágico.",
            film.getReleaseYear(), film.getAgeRating()
        );

        filmService.update(film.getId(), updatedFilmDTO);

        // Verify the changes have been saved
        Film updatedFilm = filmRepository.findById(film.getId()).orElseThrow();

        assertEquals("Chihiro y el mundo espiritual", updatedFilm.getTitle());
        assertEquals("Una aventura inolvidable en un mundo mágico.", updatedFilm.getSynopsis());
        // Verify the Image
        Blob updatedPosterBlob = updatedFilm.getPosterFile();
            // Extract byte arrays from the Blobs
        @SuppressWarnings("null")
        byte[] posterBytes = posterBlob.getBytes(1, (int) posterBlob.length());
        byte[] updatedPosterBytes = updatedPosterBlob.getBytes(1, (int) updatedPosterBlob.length());
            // Compare the byte arrays
        assertArrayEquals(posterBytes, updatedPosterBytes);
        assertEquals(2, updatedFilm.getUsersThatLiked().size());
        
        // Optional assertions to verify that the film was saved correctly
        assertEquals(2001, updatedFilm.getReleaseYear());
        assertEquals("+12", updatedFilm.getAgeRating());
    }
}
