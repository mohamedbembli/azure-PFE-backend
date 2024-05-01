package tn.dymes.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.dymes.store.entites.MyOrder;

import java.util.List;

@Repository
public interface MyOrderRepository extends JpaRepository<MyOrder, Long> {

}
