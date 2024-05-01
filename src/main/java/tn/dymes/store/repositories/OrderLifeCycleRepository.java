package tn.dymes.store.repositories;

import tn.dymes.store.entites.OrderLifeCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderLifeCycleRepository extends JpaRepository<OrderLifeCycle, Long> {

    OrderLifeCycle findByPosition(Integer position);

    List<OrderLifeCycle> findAllByOrderByPositionAsc();
}
