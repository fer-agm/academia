package com.academia.pago_service.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import com.academia.pago_service.model.Pago;
import com.academia.pago_service.service.PagoService;



import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academia.pago_service.dto.PagoDto;




@RestController
@RequestMapping("/api/pagos")
public class PagoController {
    private final PagoService pagoService;
    private static final Logger logger = LoggerFactory.getLogger(PagoController.class);

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    public ResponseEntity<PagoDto> crearPago(@Valid @RequestBody PagoDto pagoDto) {
        try {
            logger.info("POST /pagos - Creando pago: idPago={}, totalPagar={}", 
                pagoDto.getId_pago(), pagoDto.getTotalPagar());
            Pago nuevoPago = pagoService.guardar(pagoDto.toModel());
            logger.info("Pago creado exitosamente id={}", nuevoPago.getId_pago());
            return ResponseEntity.ok(PagoDto.fromModel(nuevoPago));
        } catch (Exception e) {
            logger.error("Error al crear pago: {}", e.getMessage(), e);
            throw e;
        }
    }


    @GetMapping
    public ResponseEntity<List<PagoDto>> listarPagos() {
        logger.info("GET /pagos - Listando pagos");
        List<Pago> pagos = pagoService.listar();
        List<PagoDto> dtos = pagos.stream().map(PagoDto::fromModel).collect(Collectors.toList());
        logger.info("Total pagos listados: {}", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoDto> obtenerPago(@PathVariable Long id) {
        logger.info("GET /pagos/{} - Obteniendo pago", id);
        try {
            Pago pago = pagoService.obtenerPorId(id);
            logger.info("Pago obtenido id={}", id);
            return ResponseEntity.ok(PagoDto.fromModel(pago));
        } catch (Exception e) {
            logger.error("Error al obtener pago id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagoDto> actualizarPago(@PathVariable Long id, @Valid @RequestBody PagoDto pagoDto) {
        logger.info("PUT /pagos/{} - Actualizando pago", id);
        try {
            Pago actualizado = pagoService.actualizar(id, pagoDto.toModel());
            logger.info("Pago actualizado exitosamente id={}", id);
            return ResponseEntity.ok(PagoDto.fromModel(actualizado));
        } catch (Exception e) {
            logger.error("Error al actualizar pago id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarPago(@PathVariable Long id) {
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