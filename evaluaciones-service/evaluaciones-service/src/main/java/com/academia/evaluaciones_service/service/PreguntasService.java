package com.academia.evaluaciones_service.service;

import org.springframework.stereotype.Service;

import com.academia.evaluaciones_service.model.Preguntas;
import com.academia.evaluaciones_service.repository.PreguntasRepository;

@Service

public class PreguntasService {
    private final PreguntasRepository preguntasRepository;

    public PreguntasService(PreguntasRepository preguntasRepository){
        this.preguntasRepository = preguntasRepository;

}
public Preguntas guardar (Preguntas preguntas){
        return preguntasRepository.save(preguntas);
    }


    public void borrar (Preguntas preguntas){
        preguntasRepository.delete(preguntas);
    }




}
