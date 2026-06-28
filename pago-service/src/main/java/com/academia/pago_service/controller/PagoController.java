package com.academia.pago_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academia.pago_service.dto.PagoDto;
import com.academia.pago_service.model.Pago;
import com.academia.pago_service.service.PagoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;




@Tag(name = "Pagos", description = "Gestión de pagos de cursos: creación, consulta, actualización y eliminación")
@RestController
@RequestMapping("/api/pagos")
public class PagoController {
    private final PagoService pagoService;
    private static final Logger logger = LoggerFactory.getLogger(PagoController.class);

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @Operation(summary = "Crear un nuevo pago",
            description = "Registra un nuevo pago. El IVA, total a pagar y la fecha se calculan automáticamente. "
                    + "Valida la existencia del curso asociado antes de persistir.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o curso no validable"),
            @ApiResponse(responseCode = "404", description = "Curso asociado no existe")
    })
    @PostMapping
    public ResponseEntity<EntityModel<PagoDto>> crearPago(@Valid @RequestBody PagoDto pagoDto,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        try {
            logger.info("POST /pagos - Creando pago: idPago={}, totalPagar={}",
                pagoDto.getId_pago(), pagoDto.getTotalPagar());
            Pago nuevoPago = pagoService.guardar(pagoDto.toModel(), authHeader);
            logger.info("Pago creado exitosamente id={}", nuevoPago.getId_pago());
            PagoDto creado = PagoDto.fromModel(nuevoPago);
            EntityModel<PagoDto> model = EntityModel.of(creado,
                    linkTo(methodOn(PagoController.class).obtenerPago(creado.getId_pago())).withSelfRel());
            return ResponseEntity.ok(model);
        } catch (Exception e) {
            logger.error("Error al crear pago: {}", e.getMessage(), e);
            throw e;
        }
    }


    @Operation(summary = "Listar todos los pagos",
            description = "Devuelve la lista completa de pagos registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida correctamente")
    })
    @GetMapping("/listar")
    public CollectionModel<EntityModel<PagoDto>> listarPagos() {
        logger.info("GET /pagos - Listando pagos");
        List<Pago> pagos = pagoService.listar();
        List<EntityModel<PagoDto>> dtos = pagos.stream()
                .map(PagoDto::fromModel)
                .map(dto -> EntityModel.of(dto,
                        linkTo(methodOn(PagoController.class).obtenerPago(dto.getId_pago())).withSelfRel()))
                .collect(Collectors.toList());
        logger.info("Total pagos listados: {}", dtos.size());
        return CollectionModel.of(dtos,
                linkTo(methodOn(PagoController.class).listarPagos()).withSelfRel());
    }

    @Operation(summary = "Obtener un pago por ID",
            description = "Devuelve el pago correspondiente al identificador indicado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "Pago no existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PagoDto>> obtenerPago(
            @Parameter(description = "Identificador único del pago") @PathVariable Long id) {
        logger.info("GET /pagos/{} - Obteniendo pago", id);
        try {
            Pago pago = pagoService.obtenerPorId(id);
            logger.info("Pago obtenido id={}", id);
            PagoDto dto = PagoDto.fromModel(pago);
            EntityModel<PagoDto> model = EntityModel.of(dto,
                    linkTo(methodOn(PagoController.class).obtenerPago(dto.getId_pago())).withSelfRel(),
                    linkTo(methodOn(PagoController.class).listarPagos()).withRel("listar"));
            return ResponseEntity.ok(model);
        } catch (Exception e) {
            logger.error("Error al obtener pago id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Actualizar un pago existente",
            description = "Actualiza los datos de un pago. El IVA y el total a pagar se recalculan automáticamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Pago no existe")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<PagoDto>> actualizarPago(
            @Parameter(description = "Identificador único del pago a actualizar") @PathVariable Long id,
            @Valid @RequestBody PagoDto pagoDto) {
        logger.info("PUT /pagos/{} - Actualizando pago", id);
        try {
            Pago actualizado = pagoService.actualizar(id, pagoDto.toModel());
            logger.info("Pago actualizado exitosamente id={}", id);
            PagoDto dto = PagoDto.fromModel(actualizado);
            EntityModel<PagoDto> model = EntityModel.of(dto,
                    linkTo(methodOn(PagoController.class).obtenerPago(dto.getId_pago())).withSelfRel());
            return ResponseEntity.ok(model);
        } catch (Exception e) {
            logger.error("Error al actualizar pago id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Eliminar un pago",
            description = "Elimina el pago correspondiente al identificador indicado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Pago no existe")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarPago(
            @Parameter(description = "Identificador único del pago a eliminar") @PathVariable Long id) {
        logger.info("DELETE /pagos/{} - Eliminando pago", id);
        try {
            pagoService.eliminar(id);
            logger.info("Pago eliminado exitosamente id={}", id);
            return ResponseEntity.ok("Pago Eliminado Exitosamente");
        } catch (Exception e) {
            logger.error("Error al eliminar pago id={}: {}", id, e.getMessage());
            throw e;
        }
    }
}