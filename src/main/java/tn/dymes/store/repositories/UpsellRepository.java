package tn.dymes.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.dymes.store.entites.Upsell;

@Repository
public interface UpsellRepository extends JpaRepository<Upsell, Long> {
}
