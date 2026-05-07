package com.academia.evaluaciones_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.academia.evaluaciones_service.model.Alternativas;
import com.academia.evaluaciones_service.repository.AlternativasRepository;

@Service
public class AlternativasService {

    private final AlternativasRepository alternativasRepository;

    public AlternativasService(AlternativasRepository alternativasRepository){
        this.alternativasRepository = alternativasRepository;        
    }

    public List<Alternativas> getAll() {
        return alternativasRepository.findAll();
    }

    public Alternativas guardar (Alternativas alternativas){
        return alternativasRepository.save(alternativas);
    }

    public void borrar (Long id){
        alternativasRepository.deleteById(id);
    }       

    public Optional<Alternativas> getById(Long id) {
        return alternativasRepository.findById(id);
    }




}
