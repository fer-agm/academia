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
}
