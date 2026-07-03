package com.academia.mensajeria_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.academia.mensajeria_service.model.Mensaje;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByIdEmisor(String idEmisor);
    List<Mensaje> findByIdReceptor(String idReceptor);

}