package es.codeurjc.web.nitflex.controller.rest;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.web.nitflex.dto.film.CreateFilmRequest;
import es.codeurjc.web.nitflex.dto.film.FilmDTO;
import es.codeurjc.web.nitflex.dto.film.FilmSimpleDTO;
import es.codeurjc.web.nitflex.dto.review.CreateReviewRequest;
import es.codeurjc.web.nitflex.service.FilmService;
import es.codeurjc.web.nitflex.service.ReviewService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/films")
public class FilmRestController {

	@Autowired
	private FilmService filmService;

	@Autowired
	private ReviewService reviewService;

	@GetMapping("/")
	public Collection<FilmDTO> getFilms(){
		return filmService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<FilmDTO> getFilm(@PathVariable long id) {
		
		Optional<FilmDTO> op = filmService.findOne(id);
		if(op.isPresent()) {
			FilmDTO film = op.get();
			return new ResponseEntity<>(film, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}

	@PostMapping("/")
	@ResponseStatus(HttpStatus.CREATED)
	public FilmDTO createFilm(@RequestBody CreateFilmRequest film) {
		return filmService.save(film);
	}

	@PutMapping("/{id}")
	public ResponseEntity<FilmDTO> updateFilm(@PathVariable long id, @RequestBody FilmSimpleDTO updatedFilm) {

		if (filmService.exist(id)) {
			FilmDTO film = filmService.update(id, updatedFilm);
			return new ResponseEntity<>(film, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<FilmDTO> deleteFilm(@PathVariable long id) {
		filmService.delete(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);

	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("{filmId}/reviews/")
	public FilmDTO addReview(@PathVariable long filmId, @Valid @RequestBody CreateReviewRequest review) {
		return reviewService.addReview(filmId, review);
	}

	@DeleteMapping("{filmId}/reviews/{reviewId}")
	public ResponseEntity<FilmDTO> deleteReview(@PathVariable long filmId, @PathVariable long reviewId) {
		FilmDTO film = reviewService.deleteReview(filmId, reviewId);
		return new ResponseEntity<>(film, HttpStatus.OK);
	}


}