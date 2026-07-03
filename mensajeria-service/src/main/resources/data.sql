-- Seed de notificaciones, coherente con el resto del sistema:
--   id_mensaje-> mensajes del mensajeria-service 
--   id_emisor -> emisor del mensaje
--   id_receptor      -> receptor del mensaje
--   mensaje -> contenido del mensaje    
-- INSERT IGNORE mantiene el seed idempotente entre reinicios.

INSERT IGNORE INTO mensajes (id_mensaje, id_emisor, id_receptor,mensaje) VALUES (1, '7004888-4','10492048-9',"Hola, ¿cómo estás?");
INSERT IGNORE INTO mensajes (id_mensaje, id_emisor, id_receptor,mensaje) VALUES (2, '10492048-9','7004888-4',"pésimo :(");