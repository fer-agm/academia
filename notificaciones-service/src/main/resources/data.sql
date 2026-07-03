-- Seed de notificaciones, coherente con el resto del sistema:
--   id_notificacion-> notificaciones del notificaciones-service 
--   id_estudiante -> RUN de los perfiles del user-service
--   id_certificados      -> certificados del certificado-service
-- INSERT IGNORE mantiene el seed idempotente entre reinicios.

INSERT IGNORE INTO notificaciones (id_notificacion, id_estudiante, id_certificado) VALUES (1, '7004888-4',1);
INSERT IGNORE INTO notificaciones (id_notificacion, id_estudiante, id_certificado) VALUES (2, '10492048-9', 2);

