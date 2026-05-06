package com.academia.evaluaciones_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.academia.evaluaciones_service.dto.AlternativasDTO;
import com.academia.evaluaciones_service.dto.alternativasDTO;
import com.academia.evaluaciones_service.model.Alternativas;
import com.academia.evaluaciones_service.service.AlternativasService;   

import java.util.List;
import java.util.stream.Collectors; 

@RestController
@RequestMapping("/alternativas")


public class AlternativasController {
    private final AlternativasService alternativasService;

    public AlternativasController(AlternativasService alternativasService){
        this.alternativasService = alternativasService;
    }

    @PostMapping
    public ResponseEntity<AlternativasDTO> crearAlternativa (@RequestBody AlternativasDTO alternativasDto ){
        
    }


}
