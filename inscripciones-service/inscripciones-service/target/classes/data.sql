
--  INSERT IGNORE INTO inscripciones (id_inscripcion, id_estudiante, id_curso, fecha_inscripcion, estado) VALUES (10,'13828053-5',20,'2020-12-05','INACTIVA');
-- INSERT IGNORE INTO inscripciones (id_inscripcion, id_estudiante, id_curso, fecha_inscripcion, estado) VALUES (20,'6760604-3',30,'2026-05-07','ACTIVA');

drop table inscripciones;
INSERT IGNORE INTO cupos (id_cupo, id_curso, num_maximo, num_disponible) VALUES (1,20,20,18);
INSERT IGNORE INTO cupos (id_cupo, id_curso, num_maximo, num_disponible) VALUES (2,30,20,19);