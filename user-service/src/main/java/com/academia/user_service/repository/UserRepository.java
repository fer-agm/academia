package com.academia.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.academia.user_service.model.User;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByRun(String run);

    Optional<User> findByEmail(String email);

    //ocupamos optional para evitar nullpointerexception, ya que el 
    // findbyid puede no encontrar un usuario con ese id, entonces 
    // devuelve un optional vacio, y con el orElse(null) en el service, 
    // si el optional esta vacio, devuelve null, evitando asi nullpointerexception.
}