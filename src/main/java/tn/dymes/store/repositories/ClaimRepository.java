package tn.dymes.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.dymes.store.entites.Claim;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
}
