package com.academia.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.academia.user_service.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    // Para buscar un rol por su nombre exacto
    Optional<Rol> findByNombreRol(String nombre_rol);
}
