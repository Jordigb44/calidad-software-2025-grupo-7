package es.codeurjc.web.nitflex.service.exceptions;

public class FilmNotFoundException extends RuntimeException {
    public FilmNotFoundException(long id) {
        super("Film not found with id: " + id);
    }
}
