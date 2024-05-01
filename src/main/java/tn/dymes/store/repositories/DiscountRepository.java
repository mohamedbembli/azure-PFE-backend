package tn.dymes.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tn.dymes.store.entites.Discount;
@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Discount d WHERE d.product.id = :productId")
    void deleteDiscountsByProductId(@Param("productId") long productId);
}
