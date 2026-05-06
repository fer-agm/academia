package com.academia.evaluaciones_service.service;

import org.springframework.stereotype.Service;

import com.academia.evaluaciones_service.model.Alternativas;
import com.academia.evaluaciones_service.repository.AlternativasRepository;
import java.util.List;

@Service
public class AlternativasService {

    private final AlternativasRepository alternativasRepository;

    public AlternativasService(AlternativasRepository alternativasRepository){
        this.alternativasRepository = alternativasRepository;        
    }

    public List<Alternativas> getAllAlternativas() {
        return alternativasRepository.findAll();
    }

}
