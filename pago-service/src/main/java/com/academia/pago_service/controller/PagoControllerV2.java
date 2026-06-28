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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "Pagos V2 (HATEOAS)", description = "Consulta de pagos con representación HATEOAS (enlaces de navegación)")
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

@Operation(summary = "Listar pagos (HATEOAS)",
        description = "Devuelve todos los pagos con enlaces de navegación HATEOAS.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida correctamente")
})
@GetMapping
public CollectionModel<EntityModel<Pago>> listarPagos(){
    logger.info("V2 GET /pagos - listando pagos");
        List<EntityModel<Pago>> pagos = pagoService.listar().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(pagos, linkTo(methodOn(PagoControllerV2.class).listarPagos()).withSelfRel());
    }

@Operation(summary = "Obtener un pago por ID (HATEOAS)",
        description = "Devuelve un pago con enlaces de navegación HATEOAS.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago encontrado"),
        @ApiResponse(responseCode = "404", description = "Pago no existe")
})
@GetMapping("/{id}")
    public EntityModel<Pago> obtenerPago(
            @Parameter(description = "Identificador único del pago") @PathVariable Long id) {
        logger.info("V2 GET /pagos/{} - Obteniendo pago", id);
        Pago pago = pagoService.obtenerPorId(id);
        return assembler.toModel(pago);
    }


}
