package tn.dymes.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.dymes.store.entites.Timer;

@Repository
public interface TimerRepository extends JpaRepository<Timer, Long> {
}
