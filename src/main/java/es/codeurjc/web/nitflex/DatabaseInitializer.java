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

		if(!isRunningTest()) {
			User michel = new User("Michel","michel.maes@urjc.es");
			User raul = new User("Raúl","raul@urjc.es");
			User sergio = new User("Sergio","sergio@urjc.es");
			userRepository.save(michel);
			userRepository.save(raul);
			userRepository.save(sergio);

			FilmDTO oppenheimer = saveFilmWithURLImage(
				new CreateFilmRequest("Oppenheimer", "Película sobre el físico J. Robert Oppenheimer y su papel como desarrollador de la bomba atómica. Basada en el libro 'American Prometheus: The Triumph and Tragedy of J. Robert Oppenheimer' de Kai Bird y Martin J. Sherwin.",2023, "+18"),
			"images/op.jpg"
			);

			CreateReviewRequest review = new CreateReviewRequest("Muy buena película, me ha encantado", 10);
			reviewService.addReview(oppenheimer.id(), review);

			saveFilmWithURLImage(
				new CreateFilmRequest("Barbie", "Barbie vive en Barbieland donde todo es ideal y lleno de música y color. Un buen día decide conocer el mundo real. Cuando el CEO de Mattel se entere, tratará de evitarlo a toda costa y devolver a Barbie a una caja.",2023, "+12"),
			"images/bb.jpg"
			);

			saveFilmWithURLImage(
				new CreateFilmRequest("Spider-Man: Cruzando el Multiverso", "Miles Morales regresa para una aventura épica que transportará al amigable vecino de Brooklyn Spider-Man a través del Multiverso para unir fuerzas con Gwen Stacy y un nuevo equipo de Spider-People, y enfrentarse así a un villano mucho más poderoso que cualquier cosa que hayan conocido antes.",2023, "+7"),
				"images/sp.jpg"
			);

			saveFilmWithURLImage(
				new CreateFilmRequest("Five Nights at Freddy's", "Un guardia de seguridad con problemas comienza a trabajar en Freddy Fazbear's Pizza. Mientras pasa su primera noche en el trabajo, se da cuenta de que el turno de noche en Freddy's no será tan fácil de superar.",2023, "+18"),
				"images/fnaf.jpg"
			);

			saveFilmWithURLImage(
				new CreateFilmRequest("Misión: Imposible - Sentencia mortal parte uno", "Ethan Hunt (Tom Cruise) y la IMF emprenden la misión más peligrosa a la que nunca se han enfrentado: rastrear una nueva y aterradora arma que amenaza a toda la humanidad antes de que caiga en las manos de un enemigo todopoderoso y misterioso.",2023,"+12"),
				"images/mi.jpg"
			);

			saveFilmWithURLImage(new CreateFilmRequest("Dune", "En un lejano futuro, la galaxia conocida es gobernada mediante un sistema feudal de casas nobles bajo el mandato del Emperador.", 2021, "+12"),
			"images/dune.jpg"
			);

			saveFilmWithURLImage(new CreateFilmRequest("Interstellar", "Narra las aventuras de un grupo de exploradores que hacen uso de un agujero de gusano recientemente descubierto para superar las limitaciones de los viajes espaciales tripulados y vencer las inmensas distancias que tiene un viaje interestelar.", 2014,"+7"),
			"images/in.jpg");

			saveFilmWithURLImage(
				new CreateFilmRequest("Django", "Dos años antes de estallar la Guerra Civil (1861-1865), Schultz, un cazarrecompensas alemán que le sigue la pista a unos asesinos, le promete al esclavo Django dejarlo en libertad si le ayuda a atraparlos.", 2013, "+18"),
			"images/dj.jpg"
			);
		}
	}

	private FilmDTO saveFilmWithURLImage(CreateFilmRequest film, String localpath) throws IOException {
		return filmService.save(film, imageUtils.localImageToBlob(localpath));
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
