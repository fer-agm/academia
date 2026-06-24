package com.academia.inscripciones_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.academia.inscripciones_service.model.Cupos;
import com.academia.inscripciones_service.repository.CuposRepository;
import java.util.List;

@Slf4j
@Service
public class CuposService {

    private final CuposRepository cuposRepository;

    public CuposService(CuposRepository cuposRepository) {
        this.cuposRepository = cuposRepository;
    }

    public List<Cupos> listarTodos() {
        log.info("[CuposService] Obteniendo todos los cupos");
        return cuposRepository.findAll();
    }

    public Cupos buscarPorId(Long id) {
        log.info("[CuposService] Buscando cupo con ID: {}", id);
        Cupos cupo = cuposRepository.findById(id).orElse(null);
        if (cupo == null) {
            log.warn("[CuposService] Cupo con ID {} no encontrado", id);
        }
        return cupo;
    }

    public Cupos guardarCupo(Cupos cupo) {
        log.info("[CuposService] Guardando cupo para curso ID: {}", cupo.getIdCurso());
        Cupos saved = cuposRepository.save(cupo);
        log.info("[CuposService] Cupo guardado con ID: {}", saved.getId_cupo());
        return saved;
    }

    public Cupos obtenerPorCurso(Long idCurso) {
        log.info("[CuposService] Buscando cupos del curso ID: {}", idCurso);
        return cuposRepository.findByIdCurso(idCurso).orElse(null);
    }

    public Cupos actualizar(Long id, Cupos datos) {
        log.info("[CuposService] Actualizando cupo con ID: {}", id);
        return cuposRepository.findById(id).map(cupo -> {
            cupo.setNum_maximo(datos.getNum_maximo());
            cupo.setNum_disponible(datos.getNum_disponible());
            Cupos updated = cuposRepository.save(cupo);
            log.info("[CuposService] Cupo con ID {} actualizado", id);
            return updated;
        }).orElse(null);
    }

    public boolean eliminar(Long id) {
        log.info("[CuposService] Eliminando cupo con ID: {}", id);
        if (cuposRepository.existsById(id)) {
            cuposRepository.deleteById(id);
            log.info("[CuposService] Cupo con ID {} eliminado", id);
            return true;
        }
        log.warn("[CuposService] Cupo con ID {} no encontrado para eliminar", id);
        return false;
    }

    public boolean reducirCupo(Long idCurso) {
        log.info("[CuposService] Reduciendo cupo del curso ID: {}", idCurso);
        Cupos cupo = obtenerPorCurso(idCurso);
        if (cupo != null && cupo.getNum_disponible() > 0) {
            cupo.setNum_disponible(cupo.getNum_disponible() - 1);
            cuposRepository.save(cupo);
            log.info("[CuposService] Cupo reducido. Disponibles: {}", cupo.getNum_disponible());
            return true;
        }
        log.warn("[CuposService] No hay cupos disponibles para curso ID: {}", idCurso);
        return false;
    }
}