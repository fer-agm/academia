package com.academia.pago_service.controller;


import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academia.pago_service.assemblers.PagoModelAssembler;
import com.academia.pago_service.model.Pago;
import com.academia.pago_service.service.PagoService;


@RestController
@RequestMapping("api/pagos/v2")
public class PagoControllerV2 {
    private final PagoService pagoService;
    private final PagoModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(PagoControllerV2.class);

public PagoControllerV2(PagoService pagoService, PagoModelAssembler assembler){
    this.pagoService = pagoService;
    this.assembler = assembler;
}

@GetMapping
public CollectionModel<EntityModel<Pago>> listarPagos(){
    logger.info("V2 GET /pagos - listando pagos");
        List<EntityModel<Pago>> pagos = pagoService.listar().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(pagos, linkTo(methodOn(PagoControllerV2.class).listarPagos()).withSelfRel());
    }

@GetMapping("/{id}")
    public EntityModel<Pago> obtenerPago(@PathVariable Long id) {
        logger.info("V2 GET /pagos/{} - Obteniendo pago", id);
        Pago pago = pagoService.obtenerPorId(id);
        return assembler.toModel(pago);
    }


}
