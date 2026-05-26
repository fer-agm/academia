INSERT IGNORE INTO pagos (id_pago, run_estudiante, id_curso, monto, estado, fecha) VALUES (10,'13828053-5',20,150000,'APROBADO','2020-12-05');
INSERT IGNORE INTO pagos (id_pago, run_estudiante, id_curso, monto, estado, fecha) VALUES (20,'6760604-3',30,150000,'APROBADO','2026-05-07');

INSERT IGNORE INTO transacciones (id_transaccion, metodo, fecha) VALUES (10,'TRANSFERENCIA','2020-12-05');
INSERT IGNORE INTO transacciones (id_transaccion, metodo, fecha) VALUES (20,'DEBITO','2026-05-07');