package tn.dymes.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.dymes.store.entites.Attribute;

@Repository
public interface AttributeRepository  extends JpaRepository<Attribute, Long> {
}
