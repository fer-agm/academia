package com.academia.evaluaciones_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.academia.evaluaciones_service.model.Preguntas;
import com.academia.evaluaciones_service.repository.PreguntasRepository;

@Service

public class PreguntasService {
    private final PreguntasRepository preguntasRepository;

    public PreguntasService(PreguntasRepository preguntasRepository){
        this.preguntasRepository = preguntasRepository;

}

public List<Preguntas> getAll() {
    return preguntasRepository.findAll();
}

public Optional<Preguntas> getById(Long id) {
    return preguntasRepository.findById(id);
}

public Preguntas guardar (Preguntas preguntas){
        return preguntasRepository.save(preguntas);
    }


    public void borrar (Long id){
        preguntasRepository.deleteById(id);
    }




}
