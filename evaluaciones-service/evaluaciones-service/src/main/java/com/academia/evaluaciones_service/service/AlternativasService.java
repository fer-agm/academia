package com.academia.evaluaciones_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.academia.evaluaciones_service.model.Alternativas;
import com.academia.evaluaciones_service.repository.AlternativasRepository;

@Service
public class AlternativasService {

    private final AlternativasRepository alternativasRepository;

    public AlternativasService(AlternativasRepository alternativasRepository){
        this.alternativasRepository = alternativasRepository;        
    }

    public List<Alternativas> getAllAlternativas() {
        return alternativasRepository.findAll();
    }

    public Alternativas guardar (Alternativas alternativas){
        return alternativasRepository.save(alternativas);
    }

    public void borrar (Long id){
        alternativasRepository.deleteById(id);
    }       




}
