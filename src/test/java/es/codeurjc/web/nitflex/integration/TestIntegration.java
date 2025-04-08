package es.codeurjc.web.nitflex.integration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.nitflex.ImageTestUtils;
import es.codeurjc.web.nitflex.dto.film.CreateFilmRequest;
import es.codeurjc.web.nitflex.dto.film.FilmDTO;
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
                user1.setFavoriteFilms(new ArrayList<>(List.of(film)));
                user1 = userRepository.save(user1);

                user2 = new User();
                user2.setName("usuario2");
                user2.setFavoriteFilms(new ArrayList<>(List.of(film)));
                user2 = userRepository.save(user2);
        }

        // Task 1
        @Test
        @DisplayName("Cuando se añade una película con un título válido mediante FilmService, se guarda en la base de datos y se devuelve la película creada")
        void testAddFilmWithValidTitle() throws Exception {
                // // Se definen los atributos de la nueva pelicula
                // String validTitle = "Interstellar";
                // String synopsis = "Un grupo de explorers viaja a través de un agujero de gusano en el espacio";
                // int releaseYear = 2014;
                // String ageRating = "PG-13";

                CreateFilmRequest filmRequest = new CreateFilmRequest(
                                film.getTitle(),
                                film.getSynopsis(),
                                film.getReleaseYear(),
                                film.getAgeRating());

                MultipartFile sampleImage = ImageTestUtils.createSampleImage();

                FilmDTO createdFilm = filmService.save(filmRequest, sampleImage);

                // Verificación del objeto devuelto
                assertNotNull(createdFilm.id(), "El ID de la película no debería ser null");
                assertEquals(film.getTitle(), createdFilm.title(), "El título no coincide");
                assertEquals(film.getSynopsis(), createdFilm.synopsis(), "La sinopsis no coincide");
                assertEquals(film.getReleaseYear(), createdFilm.releaseYear(), "El año de lanzamiento no coincide");
                assertEquals(film.getAgeRating(), createdFilm.ageRating(), "La clasificación por edad no coincide");

                // Verificación en base de datos
                Film savedFilm = filmRepository.findById(createdFilm.id())
                                .orElseThrow(() -> new AssertionError(
                                                "La película debería existir en la base de datos"));

                assertEquals(film.getTitle(), savedFilm.getTitle(), "Título en BD no coincide");
                assertEquals(film.getSynopsis(), savedFilm.getSynopsis(), "Sinopsis en BD no coincide");
                assertEquals(film.getReleaseYear(), savedFilm.getReleaseYear(), "Año en BD no coincide");
                assertEquals(film.getAgeRating(), savedFilm.getAgeRating(), "Clasificación en BD no coincide");

                // Verificación de la imagen
                assertNotNull(savedFilm.getPosterFile(), "El póster no debería ser null");
                assertTrue(ImageTestUtils.areSameBlob(
                                new javax.sql.rowset.serial.SerialBlob(sampleImage.getBytes()),
                                savedFilm.getPosterFile()), "La imagen guardada no coincide con la enviada");
        }

        // Task 2
        @Test
        @DisplayName("Cuando se actualizan los campos 'title' y 'synopsis' de una película (SIN imagen) y con un título válido mediante FilmService, se guardan los cambios en la base de datos y se mantiene la lista de usuarios que la han marcado como favorita")
        void testUpdateFilmTitleAndSynopsisKeepsUsers() {
                // Update the film
                FilmSimpleDTO updatedFilmDTO = new FilmSimpleDTO(
                                film.getId(), "Chihiro y el mundo espiritual",
                                "Inolvidable aventura en un mundo mágico.",
                                film.getReleaseYear(), film.getAgeRating());

                filmService.update(film.getId(), updatedFilmDTO);

                // Verify the changes
                Film updatedFilm = filmRepository.findById(film.getId()).orElseThrow();

                assertNotEquals(film.getTitle(), updatedFilm.getTitle());
                assertNotEquals(film.getSynopsis(), updatedFilm.getSynopsis());
                assertEquals(2, updatedFilm.getUsersThatLiked().size());
                assertEquals(film.getReleaseYear(), updatedFilm.getReleaseYear());
                assertEquals(film.getAgeRating(), updatedFilm.getAgeRating());
        }

        // Task 3
        @Test
        @DisplayName("Cuando se actualizan los campos 'title' y 'synopsis' de una película (CON imagen) y con un título válido mediante FilmService, se guardan los cambios en la base de datos y la imagen no cambia")
        void testUpdateFilmTitleAndSynopsisAndImageDoesntChange() throws SQLException, IOException {
                // The original film is already created in the beforeEach method

                // Update the film
                Long filmId = film.getId();
                String newFilmTitle = "Chihiro y el mundo espiritual";
                String newFilmSynopsis = "Inolvidable aventura en un mundo mágico.";
                Integer releaseYear = film.getReleaseYear();
                String ageRating = film.getAgeRating();

                FilmSimpleDTO updatedFilmDTO = new FilmSimpleDTO(
                        filmId,
                        newFilmTitle,
                        newFilmSynopsis,
                        releaseYear,
                        ageRating);
                filmService.update(filmId, updatedFilmDTO);

                // Get the updated film
                Film updatedFilm = filmRepository.findById(filmId).orElseThrow();
                
                // Verify the changes
                assertNotEquals(film.getTitle(), updatedFilm.getTitle()); // Title should change
                assertNotEquals(film.getSynopsis(), updatedFilm.getSynopsis()); // Synopsis should change
                assertEquals(2, updatedFilm.getUsersThatLiked().size()); // Users that liked should remain the same
                assertEquals(film.getReleaseYear(), updatedFilm.getReleaseYear()); // Release year should remain the same
                assertEquals(film.getAgeRating(), updatedFilm.getAgeRating()); // Age rating should remain the same
                assertTrue(ImageTestUtils.areSameBlob(
                        film.getPosterFile(),
                        updatedFilm.getPosterFile()
                        ), "The image should be the same after update."); // Verify that the poster image remains the same
        }

        // Task 4
        @Test
        @DisplayName("Cuando se borra una película que existe mediante FilmService, se elimina del repositorio y se elemina de la lista de pelícuals favoritas de los usuarios")
        void testDeleteFilmREmovesFromRepositoryAndUsersFavoriteFilms() {
                // Verify that the film exists before deltetion
                Film existingFilm = filmRepository.findById(film.getId())
                                .orElseThrow(() -> new AssertionError(
                                                "La película debería existir en la base de datos"));
                assertNotNull(existingFilm, "La película debería existir en la base de datos antes de eliminarla");

                // Verify that the film is in the users favorite films
                assertTrue(user1.getFavoriteFilms().contains(existingFilm),
                                "La película debería estar en los favoritos del usuario 1 antes de eliminarla");
                assertTrue(user2.getFavoriteFilms().contains(existingFilm),
                                "La película debería estar en los favoritos del usuario 2 antes de eliminarla");

                // Delete the film
                filmService.delete(film.getId());

                // Verify the deletion from the repository
                assertTrue(filmRepository.findById(film.getId()).isEmpty(), "La película debería haber sido eliminada");

                // Verify that the film is removed from the users' favorite films
                user1 = userRepository.findById(user1.getId()).orElseThrow();
                user2 = userRepository.findById(user2.getId()).orElseThrow();

                assertFalse(user1.getFavoriteFilms().contains(existingFilm),
                                "El usuario1 no debe tener la película como favorita después de la eliminación");
                assertFalse(user2.getFavoriteFilms().contains(existingFilm),
                                "El usuario2 no debe tener la película como favorita después de la eliminación");

                // Todo el anterior código se podría sustituir por:

                // assertEquals(0, user1.getFavoriteFilms().size());
                // assertEquals(0, user2.getFavoriteFilms().size());
        } // Si previamente estamos seguros de el único favorito que tenínan era la
          // película eliminada y no hay más como favoritos
}
