package com.academia.pago_service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.academia.pago_service.model.Pago;
import com.academia.pago_service.repository.PagoRepository;

import net.datafaker.Faker;

/**
 * Genera pagos de prueba con la librería Faker al arrancar el servicio.
 *
 * <p>A diferencia de un faker "ciego", aquí los campos que son referencias
 * ({@code runEstudiante} e {@code idCurso}) se eligen de estudiantes y cursos
 * que SÍ existen en el sistema, para no romper la integridad referencial ni
 * ensuciar el listado con datos que no cuadran con el resto.</p>
 *
 * <p>Solo siembra cuando la base viene recién creada (únicamente el seed de
 * data.sql). Así un reinicio del contenedor no acumula pagos infinitamente.</p>
 */
@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    // RUN de estudiantes reales (rol alumno) sembrados en user-service.
    private static final List<String> RUNS_ESTUDIANTES = List.of(
            "13828053-5", "6760604-3", "5701779-1", "10985515-4", "19429788-2", "20076124-3");

    // Cursos que existen (clases / inscripciones / evaluaciones).
    private static final List<Long> CURSOS = List.of(10L, 20L, 30L);

    // Cantidad de filas que deja data.sql; si hay más, ya se sembró antes.
    private static final long FILAS_SEED = 2;

    private static final int PAGOS_A_GENERAR = 5;

    private final PagoRepository pagoRepository;

    DataLoader(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    @Override
    public void run(String... args) {
        if (pagoRepository.count() > FILAS_SEED) {
            log.info("DataLoader: la base ya tiene pagos generados; no se vuelve a sembrar.");
            return;
        }

        Faker faker = new Faker();
        Random random = new Random();

        for (int i = 0; i < PAGOS_A_GENERAR; i++) {
            Pago pago = new Pago();
            // Referencias coherentes: estudiante y curso que existen de verdad.
            pago.setRunEstudiante(RUNS_ESTUDIANTES.get(random.nextInt(RUNS_ESTUDIANTES.size())));
            pago.setIdCurso(CURSOS.get(random.nextInt(CURSOS.size())));

            // Montos aleatorios pero con aritmética válida (igual escala que el seed).
            int valorNeto = faker.number().numberBetween(50000, 200000);
            int descuento = faker.number().numberBetween(0, 30);
            int subtotal = valorNeto - (valorNeto * descuento / 100);
            int iva = subtotal * 19 / 100;
            int totalPagar = subtotal + iva;

            pago.setValorNeto(valorNeto);
            pago.setDescuento(descuento);
            pago.setIva(iva);
            pago.setTotalPagar(totalPagar);
            pago.setMedioPago(faker.options().option("TRANSFERENCIA", "DEBITO", "CREDITO", "EFECTIVO"));
            pago.setFecha(LocalDateTime.now().minusDays(random.nextInt(1000)));

            pagoRepository.save(pago);
        }
        log.info("DataLoader: {} pagos de prueba generados con Faker (datos coherentes).", PAGOS_A_GENERAR);
    }
}
