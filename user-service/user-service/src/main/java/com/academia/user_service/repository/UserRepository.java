package com.academia.user_service.repository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.academia.user_service.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    Optional<User> findByRun(String run);
    
    Optional<User> findByEmail(String email);
    //ocupamos optional para evitar nullpointerexception, ya que el 
    // findbyid puede no encontrar un usuario con ese id, entonces 
    // devuelve un optional vacio, y con el orElse(null) en el service, 
    // si el optional esta vacio, devuelve null, evitando asi el nullpointerexception.
}