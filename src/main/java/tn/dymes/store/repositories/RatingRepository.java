package tn.dymes.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.dymes.store.entites.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
}
