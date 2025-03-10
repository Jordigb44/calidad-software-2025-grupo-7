package es.codeurjc.web.nitflex.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.web.nitflex.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}
