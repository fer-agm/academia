INSERT IGNORE INTO pagos (id_pago, run_estudiante, id_curso,  valor_neto, iva, descuento, total_pagar,medio_pago fecha) VALUES (10,'13828053-5',20,150000,28500,0,178500,'TRANSFERENCIA','2020-12-05');
INSERT IGNORE INTO pagos (id_pago, run_estudiante, id_curso,  valor_neto, iva, descuento, total_pagar,medio_pago fecha) VALUES (20,'13828053-5',30,150000,28500,0,178500,'DEBITO','2020-12-05');


INSERT IGNORE INTO transacciones (id_transaccion, metodo, fecha) VALUES (10,'TRANSFERENCIA','2020-12-05');
INSERT IGNORE INTO transacciones (id_transaccion, metodo, fecha) VALUES (20,'DEBITO','2026-05-07');