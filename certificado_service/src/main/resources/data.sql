-- Seed de certificados emitidos al completar un curso. Coherente con:
--   id_estudiante -> RUN de los perfiles del user-service
--   id_curso      -> cursos del clases-service (mismos estudiantes/cursos que las calificaciones)
-- 'codigo' es único; INSERT IGNORE mantiene el seed idempotente entre reinicios.

INSERT IGNORE INTO certificado (id_certificado, id_estudiante, id_curso, fecha_emision, codigo) VALUES (1, '13828053-5', 20, '2021-02-01 10:00:00', 'CERT-2021-0020-13828053');
INSERT IGNORE INTO certificado (id_certificado, id_estudiante, id_curso, fecha_emision, codigo) VALUES (2, '6760604-3',  30, '2026-06-01 10:00:00', 'CERT-2026-0030-6760604');
