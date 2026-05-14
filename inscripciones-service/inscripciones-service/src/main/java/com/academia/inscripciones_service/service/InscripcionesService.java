package com.academia.inscripciones_service.service;

import com.academia.inscripciones_service.model.Inscripciones;
import com.academia.inscripciones_service.repository.InscripcionesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InscripcionesService {

    @Autowired
    private InscripcionesRepository inscripcionesRepository;


    public List<Inscripciones> listarTodas() {
        return inscripcionesRepository.findAll();
    }

    public Inscripciones buscarPorId(Long id) {
        return inscripcionesRepository.findById(id).orElse(null);
    }

    public Inscripciones crearInscripcion(Inscripciones inscripciones) {
        
        inscripciones.setFecha_inscripcion(LocalDateTime.now());
        if (inscripciones.getEstado() == null) {
            inscripciones.setEstado("ACTIVO");
        }
        return inscripcionesRepository.save(inscripciones);
    }

    public List<Inscripciones> listarPorEstudiante(String run) {
        return inscripcionesRepository.findByIdEstudiante(run);
    }

    public Inscripciones actualizar(Long id, Inscripciones datos) {
        return inscripcionesRepository.findById(id).map(ins -> {
            ins.setEstado(datos.getEstado());
            ins.setIdCurso(datos.getIdCurso());
            return inscripcionesRepository.save(ins);
        }).orElse(null);
    }

    public boolean eliminar(Long id) {
        if (inscripcionesRepository.existsById(id)) {
            inscripcionesRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
