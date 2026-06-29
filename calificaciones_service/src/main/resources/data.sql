-- Seed de calificaciones y promedios, coherente con el resto del sistema:
--   id_evaluacion -> evaluaciones del evaluaciones-service (10 = curso 20, 20 = curso 30)
--   id_estudiante -> RUN de los perfiles del user-service
--   id_curso      -> cursos del clases-service
-- Notas en escala chilena 1.0–7.0 (igual que valida CalificacionDTO/PromedioDTO).
-- INSERT IGNORE mantiene el seed idempotente entre reinicios.

-- 13828053-5 rindió la evaluación del curso 20 (Python 2); 6760604-3 la del curso 30 (Python 3).
INSERT IGNORE INTO calificaciones (id_calificacion, id_evaluacion, id_estudiante, fecha, nota) VALUES (1, 10, '13828053-5', '2021-01-15', 6.5);
INSERT IGNORE INTO calificaciones (id_calificacion, id_evaluacion, id_estudiante, fecha, nota) VALUES (2, 20, '6760604-3',  '2026-05-20', 5.8);

-- Promedio general por estudiante/curso, consistente con las notas anteriores.
INSERT IGNORE INTO promedio (id_promedio, id_estudiante, id_curso, promedio_general, total_evaluaciones) VALUES (1, '13828053-5', 20, 6.5, 1);
INSERT IGNORE INTO promedio (id_promedio, id_estudiante, id_curso, promedio_general, total_evaluaciones) VALUES (2, '6760604-3',  30, 5.8, 1);
