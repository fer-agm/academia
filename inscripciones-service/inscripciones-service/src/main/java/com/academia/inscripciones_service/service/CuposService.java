package com.academia.inscripciones_service.service;

import com.academia.inscripciones_service.model.Cupos;
import com.academia.inscripciones_service.repository.CuposRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CuposService {

    @Autowired
    private CuposRepository cuposRepository;

    public Cupos guardarCupo(Cupos cupo) {
        return cuposRepository.save(cupo);
    }

    public Cupos obtenerPorCurso(Long idCurso) {
        return cuposRepository.findByIdCurso(idCurso).orElse(null);
    }

    // Método clave para inscripciones
    public boolean reducirCupo(Long idCurso) {
        Cupos cupo = obtenerPorCurso(idCurso);
        if (cupo != null && cupo.getNum_disponible() > 0) {
            cupo.setNum_disponible(cupo.getNum_disponible() - 1);
            cuposRepository.save(cupo);
            return true;
        }
        return false;
    }
}