package tn.dymes.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.dymes.store.entites.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
