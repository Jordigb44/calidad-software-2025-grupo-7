package es.codeurjc.web.nitflex.model;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Range;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id = null;

    @Column(length = 50000)
    @NotBlank(message = "Review text field is mandatory")
    private String text;
    
    @NotNull(message = "Score field is mandatory")
    @Range(min = 0, max = 10, message = "Score must be between 0 and 10")
    private int score;

    @ManyToOne
    private User user;

    @ManyToOne
    private Film film;

    @CreationTimestamp
    private Date created_at;

    public Review(){
        this.id = 0L;
    }

    public Review(String text, int score) {
        this.id = 0L;
        this.text = text;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public int getScore() {
        return score;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    @Override
    public String toString() {
        return "Review [user=" + user.getName() + ", text=" + text + ", score=" + score + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Review review = (Review) o;

        if (!id.equals(review.id)) return false;
        if (score != review.score) return false;
        if (!text.equals(review.text)) return false;
        if (!user.equals(review.user)) return false;
        return ((film == null) && (review.film == null)) || film.equals(review.film);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + text.hashCode();
        result = 31 * result + score;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (film != null ? film.hashCode() : 0);
        return result;
    }

}
