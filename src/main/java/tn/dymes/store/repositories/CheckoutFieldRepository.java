package tn.dymes.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.dymes.store.entites.CheckoutField;

@Repository
public interface CheckoutFieldRepository extends JpaRepository<CheckoutField, Long> {

}
