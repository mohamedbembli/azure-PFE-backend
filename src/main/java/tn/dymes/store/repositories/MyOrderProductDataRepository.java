package tn.dymes.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.dymes.store.entites.MyOrderProductData;

public interface MyOrderProductDataRepository extends JpaRepository<MyOrderProductData, Long> {

}
