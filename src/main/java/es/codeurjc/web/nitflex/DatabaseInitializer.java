package es.codeurjc.web.nitflex;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.codeurjc.web.nitflex.dto.film.CreateFilmRequest;
import es.codeurjc.web.nitflex.dto.film.FilmDTO;
import es.codeurjc.web.nitflex.dto.review.CreateReviewRequest;
import es.codeurjc.web.nitflex.model.User;
import es.codeurjc.web.nitflex.repository.UserRepository;
import es.codeurjc.web.nitflex.service.FilmService;
import es.codeurjc.web.nitflex.service.ReviewService;
import es.codeurjc.web.nitflex.utils.ImageUtils;
import jakarta.annotation.PostConstruct;

@Component
public class DatabaseInitializer {

	@Autowired
	private FilmService filmService;

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ImageUtils imageUtils;

	@PostConstruct
	public void init() throws IOException {

		if (!isRunningTest()) {
			User michel = new User("Michel", "michel.maes@urjc.es");
			User raul = new User("Raúl", "raul@urjc.es");
			User sergio = new User("Sergio", "sergio@urjc.es");
			userRepository.save(michel);
			userRepository.save(raul);
			userRepository.save(sergio);

			FilmDTO oppenheimer = saveFilmWithURLImage(
					new CreateFilmRequest("Oppenheimer",
							"Película sobre el físico J. Robert Oppenheimer y su papel como desarrollador de la bomba atómica.",
							2023, "+18"),
					"https://upload.wikimedia.org/wikipedia/en/4/4a/Oppenheimer_%28film%29.jpg?20230709000257");

			CreateReviewRequest review = new CreateReviewRequest("Muy buena película, me ha encantado", 10);
			reviewService.addReview(oppenheimer.id(), review);

			saveFilmWithURLImage(
					new CreateFilmRequest("Barbie", "Barbie vive en Barbieland... decide conocer el mundo real.", 2023,
							"+12"),
					"https://upload.wikimedia.org/wikipedia/en/0/0b/Barbie_2023_poster.jpg");

			saveFilmWithURLImage(
					new CreateFilmRequest("Spider-Man: Cruzando el Multiverso",
							"Miles Morales regresa para una aventura épica...", 2023, "+7"),
					"https://upload.wikimedia.org/wikipedia/en/b/b4/Spider-Man-_Across_the_Spider-Verse_poster.jpg?20230427001932");

			saveFilmWithURLImage(
					new CreateFilmRequest("Five Nights at Freddy's",
							"Un guardia de seguridad con problemas comienza a trabajar en Freddy Fazbear's Pizza...",
							2023, "+18"),
					"https://upload.wikimedia.org/wikipedia/en/d/d6/Five_Nights_At_Freddy%27s_poster.jpeg?20240213101327");

			saveFilmWithURLImage(
					new CreateFilmRequest("Misión: Imposible - Sentencia mortal parte uno",
							"Ethan Hunt y la IMF emprenden la misión más peligrosa...", 2023, "+12"),
					"https://upload.wikimedia.org/wikipedia/en/e/ed/Mission-_Impossible_%E2%80%93_Dead_Reckoning_Part_One_poster.jpg?20230517140125");

			saveFilmWithURLImage(
					new CreateFilmRequest("Dune",
							"En un lejano futuro, la galaxia conocida es gobernada mediante un sistema feudal...", 2021,
							"+12"),
					"https://upload.wikimedia.org/wikipedia/en/8/8e/Dune_%282021_film%29.jpg");

			saveFilmWithURLImage(
					new CreateFilmRequest("Interstellar", "Narra las aventuras de un grupo de exploradores...", 2014,
							"+7"),
					"https://upload.wikimedia.org/wikipedia/en/b/bc/Interstellar_film_poster.jpg");

			saveFilmWithURLImage(
					new CreateFilmRequest("Django",
							"Dos años antes de estallar la Guerra Civil, Schultz le promete al esclavo Django dejarlo en libertad si le ayuda a atrapar a unos asesinos.",
							2013, "+18"),
					"https://upload.wikimedia.org/wikipedia/en/8/8b/Django_Unchained_Poster.jpg");
		}
	}

	private FilmDTO saveFilmWithURLImage(CreateFilmRequest film, String imageUrl) throws IOException {
		return filmService.save(film, imageUtils.remoteImageToBlob(imageUrl));
	}

	private boolean isRunningTest() {
		try {
			Class.forName("org.junit.jupiter.api.Test");
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}

}
