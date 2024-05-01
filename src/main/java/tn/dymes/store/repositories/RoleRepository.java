package tn.dymes.store.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.dymes.store.entites.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

}
