package com.academia.inscripciones_service.service;

import com.academia.inscripciones_service.model.Cupos;
import com.academia.inscripciones_service.repository.CuposRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CuposService {

    @Autowired
    private CuposRepository cuposRepository;

    public List<Cupos> listarTodos() {
        return cuposRepository.findAll();
    }

    public Cupos buscarPorId(Long id) {
        return cuposRepository.findById(id).orElse(null);
    }

    public Cupos guardarCupo(Cupos cupo) {
        return cuposRepository.save(cupo);
    }

    public Cupos obtenerPorCurso(Long idCurso) {
        return cuposRepository.findByIdCurso(idCurso).orElse(null);
    }

    public Cupos actualizar(Long id, Cupos datos) {
        return cuposRepository.findById(id).map(cupo -> {
            cupo.setNum_maximo(datos.getNum_maximo());
            cupo.setNum_disponible(datos.getNum_disponible());
            return cuposRepository.save(cupo);
        }).orElse(null);
    }

    public boolean eliminar(Long id) {
        if (cuposRepository.existsById(id)) {
            cuposRepository.deleteById(id);
            return true;
        }
        return false;
    }

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