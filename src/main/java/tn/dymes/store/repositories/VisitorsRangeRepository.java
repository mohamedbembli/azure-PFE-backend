package tn.dymes.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.dymes.store.entites.VisitorsRange;

@Repository
public interface VisitorsRangeRepository extends JpaRepository<VisitorsRange, Long> {

}
