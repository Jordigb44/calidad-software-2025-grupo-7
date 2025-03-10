package es.codeurjc.web.nitflex.service;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.nitflex.dto.film.CreateFilmRequest;
import es.codeurjc.web.nitflex.dto.film.FilmDTO;
import es.codeurjc.web.nitflex.dto.film.FilmMapper;
import es.codeurjc.web.nitflex.dto.film.FilmSimpleDTO;
import es.codeurjc.web.nitflex.model.Film;
import es.codeurjc.web.nitflex.model.User;
import es.codeurjc.web.nitflex.repository.FilmRepository;
import es.codeurjc.web.nitflex.repository.UserRepository;
import es.codeurjc.web.nitflex.service.exceptions.FilmNotFoundException;
import es.codeurjc.web.nitflex.utils.ImageUtils;
import jakarta.transaction.Transactional;

@Service
public class FilmService {

	private FilmRepository filmRepository;

	private UserRepository userRepository;

	private ImageUtils imageUtils;

	private FilmMapper filmMapper;

	public FilmService(FilmRepository filmRepository, UserRepository userRepository, ImageUtils imageUtils, FilmMapper filmMapper) {
		this.filmRepository = filmRepository;
		this.userRepository = userRepository;
		this.imageUtils = imageUtils;
		this.filmMapper = filmMapper;
	}

	public Optional<FilmDTO> findOne(long id) {
		return filmRepository.findById(id).map(filmMapper::toDTO);
	}

	public InputStream getPosterFile(long id) {
		Film film = filmRepository.findById(id)
				.orElseThrow(() -> new FilmNotFoundException(id));
		Blob blob = film.getPosterFile();
		try {
			return blob.getBinaryStream();
		} catch (SQLException e) {
			throw new RuntimeException("Error getting image from database", e);
		}
	}

	public boolean exist(long id) {
		return filmRepository.existsById(id);
	}

	public List<FilmDTO> findAll() {
		return filmMapper.toDTO(filmRepository.findAll());
	}

	public FilmDTO save(CreateFilmRequest film, Blob imageField) {
		if (film.title() == null || film.title().isEmpty()) {
			throw new IllegalArgumentException("The title is empty");
		}
		Film newFilm = filmMapper.toDomain(film);
		newFilm.setPosterFile(imageField);
		return filmMapper.toDTO(filmRepository.save(newFilm));
	}

	public FilmDTO save(CreateFilmRequest film, MultipartFile imageField) {
		if (imageField != null && imageField.getSize() > 0) {
			return this.save(film, imageUtils.multiPartFileImageToBlob(imageField));
		}
		return this.save(film, (Blob) null);
	}

	public FilmDTO save(CreateFilmRequest film) {
		return this.save(film, (Blob) null);
	}

	public FilmDTO update(long filmId, FilmSimpleDTO film) {
		return this.update(filmId, film, null);
	}

	@Transactional
	public FilmDTO update(long filmId, FilmSimpleDTO film, MultipartFile imageField) {
		Film toUpdateFilm = filmRepository.findById(filmId)
				.orElseThrow(() -> new FilmNotFoundException(filmId));
		toUpdateFilm.setTitle(film.title());
		toUpdateFilm.setSynopsis(film.synopsis());
		toUpdateFilm.setReleaseYear(film.releaseYear());
		toUpdateFilm.setAgeRating(film.ageRating());
		if (imageField != null && imageField.getSize() > 0) {
			Blob blobImage = imageUtils.multiPartFileImageToBlob(imageField);
			toUpdateFilm.setPosterFile(blobImage);
		}
		return filmMapper.toDTO(filmRepository.save(toUpdateFilm));
	}

	@Transactional
	public void delete(long id) {
		Film film = filmRepository.findById(id)
				.orElseThrow(() -> new FilmNotFoundException(id));
		for (User u : film.getUsersThatLiked()) {
			u.getFavoriteFilms().remove(film);
			userRepository.save(u);
		}
		filmRepository.deleteById(id);
	}

}
