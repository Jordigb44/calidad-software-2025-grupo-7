package es.codeurjc.web.nitflex.model;

import java.sql.Blob;
import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class Film {
    
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

    private String title;
	
	@Column(length = 50000)
	private String synopsis;

    private int releaseYear;

    private String ageRating;

    @Lob
	private Blob posterFile;

    @OneToMany(mappedBy = "film",cascade=CascadeType.ALL)
    private List<Review> reviews;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "favoriteFilms")
    private List<User> usersThatLiked;

    public Film() {
        this.id=0L;
        this.title = "";
        this.synopsis = "";
        this.releaseYear = 0;
        this.posterFile = null;
        this.reviews = new LinkedList<>();
        this.usersThatLiked = new LinkedList<>();
        this.ageRating = "";
    }

    public Film(String title, String synopsis, int year, String ageRating) {
        this.id = 0L;
        this.title = title;
        this.synopsis = synopsis;
        this.releaseYear = year;
        this.reviews = new LinkedList<>();
        this.usersThatLiked = new LinkedList<>();
        this.ageRating = ageRating;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int year) {
        this.releaseYear = year;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public void removeReview(Review review) {
        this.reviews.remove(review);
    }

    public void addUser(User user){
        this.usersThatLiked.add(user);
    }

    public void removeUser(User user){
        this.usersThatLiked.remove(user);
    }

    public Blob getPosterFile() {
        return posterFile;
    }

    public void setPosterFile(Blob posterFile) {
        this.posterFile = posterFile;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    @Override
    public String toString() {
        return "Film [id=" + id + ", title=" + title + ", synopsis=" + synopsis + ", releaseYear=" + releaseYear
                + ", ageRating=" + ageRating + ", posterFile=" + posterFile + ", reviews=" + reviews
                + ", usersThatLiked=" + usersThatLiked + "]";
    }

    public List<User> getUsersThatLiked() {
        return this.usersThatLiked;
    }

    @Override
    public boolean equals(Object o){
        if(o == null) return false;
        if(o == this) return true;
        if(o instanceof Film){
            Film f = (Film) o;
            return f.getId().equals(this.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

}
