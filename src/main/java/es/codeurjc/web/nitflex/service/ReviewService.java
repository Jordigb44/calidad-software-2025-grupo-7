package es.codeurjc.web.nitflex.service;

import org.springframework.stereotype.Service;

import es.codeurjc.web.nitflex.dto.film.FilmDTO;
import es.codeurjc.web.nitflex.dto.film.FilmMapper;
import es.codeurjc.web.nitflex.dto.review.CreateReviewRequest;
import es.codeurjc.web.nitflex.dto.review.ReviewMapper;
import es.codeurjc.web.nitflex.model.Film;
import es.codeurjc.web.nitflex.model.Review;
import es.codeurjc.web.nitflex.repository.FilmRepository;
import es.codeurjc.web.nitflex.repository.ReviewRepository;
import es.codeurjc.web.nitflex.service.exceptions.FilmNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class ReviewService {

    private FilmRepository filmRepository;
    private UserComponent userComponent;
    private ReviewRepository reviewRepository;
    private FilmMapper filmMapper;
    private ReviewMapper reviewMapper;

    public ReviewService(FilmRepository filmRepository, UserComponent userComponent,
            ReviewRepository reviewRepository, FilmMapper filmMapper, ReviewMapper reviewMapper) {
        this.filmRepository = filmRepository;
        this.userComponent = userComponent;
        this.reviewRepository = reviewRepository;
        this.filmMapper = filmMapper;
        this.reviewMapper = reviewMapper;
    }

    @Transactional
    public FilmDTO addReview(long filmId, CreateReviewRequest reviewDto) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmNotFoundException(filmId));
        Review review = reviewMapper.toDomain(reviewDto);
        review.setFilm(film);
        review.setUser(userComponent.getUser());
        reviewRepository.save(review);
        FilmDTO response = filmMapper.toDTO(film);
        response.reviews().add(reviewMapper.toSimpleDTO(review));
        return response;
    }

    public FilmDTO deleteReview(long filmId, long reviewId) {
        reviewRepository.deleteById(reviewId);
        return filmMapper.toDTO(filmRepository.findById(filmId)
                .orElseThrow(() -> new IllegalArgumentException("Film not found")));
    }
}
