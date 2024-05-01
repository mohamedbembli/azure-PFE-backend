package tn.dymes.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.dymes.store.entites.Variant;

@Repository
public interface VariantRepository extends JpaRepository<Variant, Long> {
}
