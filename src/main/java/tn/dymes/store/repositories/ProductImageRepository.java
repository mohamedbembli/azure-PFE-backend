package tn.dymes.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tn.dymes.store.entites.ProductImage;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findAllByOrderByPositionAsc();

    @Transactional
    @Modifying
    @Query("DELETE FROM ProductImage d WHERE d.product.id = :productId")
    void deleteByProductId(@Param("productId") long productId);


}
