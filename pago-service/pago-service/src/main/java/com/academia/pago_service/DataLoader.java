package com.academia.pago_service;

import java.util.Date;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.academia.pago_service.model.Pago;
import com.academia.pago_service.repository.PagoRepository;

import net.datafaker.Faker;

@Component

public class DataLoader implements CommandLineRunner {
    private final PagoRepository pagoRepository;


    DataLoader(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker();
        Random random = new Random();
        // Generar pagos
        for (int i = 0; i < 5; i++) {
            Pago pago = new Pago();
            pago.setRunEstudiante(faker.numerify("#########-#"));
            pago.setIdCurso((long) random.nextInt(10)+1);

            int valorNeto = faker.number().numberBetween(10000, 100000);
            int descuento = faker.number().numberBetween(0, 100);
            int subtotal = valorNeto - (valorNeto * descuento / 100);                   
            int iva=subtotal * 19 / 100;
            int totalPagar=subtotal + iva;

            pago.setValorNeto(valorNeto);
            pago.setIva(iva);
            pago.setDescuento(descuento);
            pago.setTotalPagar(totalPagar);
            pago.setMedioPago(faker.options().option("Tarjeta de Crédito", "Transferencia", "Efectivo"));
            pago.setFecha(new Date());

            pagoRepository.save(pago);
        }
    }
}