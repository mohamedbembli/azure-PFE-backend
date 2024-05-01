package tn.dymes.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.dymes.store.entites.Element;

@Repository
public interface ElementRepository extends JpaRepository<Element, Long> {

}
