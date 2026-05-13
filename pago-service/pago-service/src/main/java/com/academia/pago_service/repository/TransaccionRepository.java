package com.academia.pago_service.repository;


import com.academia.pago_service.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    //agregamos algo más??? idkkk
}
