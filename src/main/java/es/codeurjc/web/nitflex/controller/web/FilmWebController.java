package es.codeurjc.web.nitflex.controller.web;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.web.nitflex.dto.film.CreateFilmRequest;
import es.codeurjc.web.nitflex.dto.film.FilmDTO;
import es.codeurjc.web.nitflex.dto.film.FilmSimpleDTO;
import es.codeurjc.web.nitflex.dto.review.CreateReviewRequest;
import es.codeurjc.web.nitflex.model.Film;
import es.codeurjc.web.nitflex.service.FavoriteFilmService;
import es.codeurjc.web.nitflex.service.FilmService;
import es.codeurjc.web.nitflex.service.ReviewService;
import es.codeurjc.web.nitflex.utils.AgeRatingOptionsUtils;
import es.codeurjc.web.nitflex.utils.AgeRatingOptionsUtils.AgeRating;
import jakarta.validation.Valid;

@Controller
public class FilmWebController {

	@Autowired
	private FilmService filmService;

	@Autowired 
	private FavoriteFilmService favoriteFilmService;

	@Autowired
	private ReviewService reviewService;
	
	@GetMapping("/")
	public String showFilms(Model model) {

		model.addAttribute("films", filmService.findAll());
		
		return "films";
	}
	
	@GetMapping("/films/{id}")
	public String showFilm(Model model, @PathVariable long id) {
		
		Optional<FilmDTO> op = filmService.findOne(id);
		if(op.isPresent()) {
			FilmDTO film = op.get();
			model.addAttribute("film", film);
			model.addAttribute("isInFavorites", favoriteFilmService.isFavorite(film));
			return "film";
		}else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found");
		}
		
	}

	@GetMapping("/films/{id}/delete")
	public String removeFilm(Model model, @PathVariable long id) {
		
		Optional<FilmDTO> op = filmService.findOne(id);
		if(op.isPresent()) {
			filmService.delete(id);
			FilmDTO removedFilm = op.get();
			model.addAttribute("error", false);
			model.addAttribute("message", "Film '"+removedFilm.title()+"' deleted");
			return "message";
		}else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found");
		}
		
	}
	
	@GetMapping("/films/new")
	public String newFilm(Model model) {
		model.addAttribute("action", "/films/new");
		model.addAttribute("film", new Film());
		model.addAttribute("ageRatings", AgeRating.values());
		return "filmForm";
	}
	
	@PostMapping("/films/new")
	public String newFilmProcess(CreateFilmRequest film, MultipartFile imageField, Model model) {

		FilmDTO newFilm = null;

		try{
			newFilm = filmService.save(film, imageField);
		}catch(IllegalArgumentException e){
			model.addAttribute("error", true);
			model.addAttribute("errors", List.of(e.getMessage()));
			model.addAttribute("action", "/films/new");
			model.addAttribute("film", film);
			return "filmForm";
		}
		
		return "redirect:/films/" + newFilm.id();
	}
	
	@GetMapping("/films/{id}/edit")
	public String editFilm(Model model, @PathVariable long id) {

		Optional<FilmDTO> op = filmService.findOne(id);
		if(op.isPresent()) {
			FilmDTO film = op.get();
			model.addAttribute("action", "/films/"+id+"/edit");
			model.addAttribute("film", film);
			model.addAttribute("ageRatings", AgeRatingOptionsUtils.getAgeRatingOptions(film.ageRating()));
			return "filmForm";
		}else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found");
		}
		
	}
	
	@PostMapping("/films/{id}/edit")
	public String editFilmProcess(Model model, @PathVariable long id, FilmSimpleDTO film, MultipartFile imageField) {

		FilmDTO updatedFilm = null;

		try{
			updatedFilm = filmService.update(id, film, imageField);
		}catch(ResponseStatusException e){
			model.addAttribute("error", true);
			model.addAttribute("errors", List.of(e.getReason()));
			model.addAttribute("action", "/films/"+id+"/edit");
			model.addAttribute("film", film);
			return "filmForm";
		}

		model.addAttribute("film", updatedFilm);
		
		return "redirect:/films/" + film.id();
	}

	@GetMapping("/films/{id}/poster")
	public ResponseEntity<Object> getMethodName(@PathVariable long id) throws IOException {
		Resource poster;
		try {
			poster = new InputStreamResource(filmService.getPosterFile(id));
		} catch (Exception e) {
			ClassPathResource resource = new ClassPathResource("static/images/no-image.png");
			byte[] imageBytes = resource.getInputStream().readAllBytes();
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(imageBytes);
		}
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(poster);
	}

	@PostMapping("/films/{filmId}/reviews")
	public String addReview(@PathVariable long filmId, @Valid CreateReviewRequest review) {
		FilmDTO film = reviewService.addReview(filmId, review);
		return "redirect:/films/" + film.id();
	}

	@PostMapping("/films/{filmId}/reviews/{reviewId}/remove")
	public String removeReview(@PathVariable long filmId, @PathVariable long reviewId) {
		FilmDTO film = reviewService.deleteReview(filmId, reviewId);
		return "redirect:/films/" + film.id();
	}

	@PostMapping("/films/{filmId}/addFavorite")
	public String addFavorite(@PathVariable long filmId) {
		favoriteFilmService.addToFavorites(filmId);
		return "redirect:/films/" + filmId;
	}

	@PostMapping("/films/{filmId}/removeFavorite")
	public String removeFavorite(@PathVariable long filmId) {
		favoriteFilmService.removeFromFavorites(filmId);
		return "redirect:/films/" + filmId;
	}

}
