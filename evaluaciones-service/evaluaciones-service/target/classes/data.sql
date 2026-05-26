INSERT IGNORE INTO evaluaciones (id_evaluacion, id_curso, punt_min, punt_max) VALUES (10,20,1,100);
INSERT IGNORE INTO evaluaciones (id_evaluacion, id_curso, punt_min, punt_max) VALUES (20,30,1,100);
INSERT IGNORE INTO evaluaciones (id_evaluacion, id_curso, punt_min, punt_max) VALUES (30,70,1,100);

INSERT IGNORE INTO preguntas (id_pregunta, enunciado, puntaje, id_Evaluacion) VALUES (10,'¿Qué es Python?',50,10);
INSERT IGNORE INTO preguntas (id_pregunta, enunciado, puntaje, id_Evaluacion) VALUES (20,'¿Qué es una variable?',20,10);
INSERT IGNORE INTO preguntas (id_pregunta, enunciado, puntaje, id_Evaluacion) VALUES (30,'¿Qué es un loop?',30,10);

INSERT IGNORE INTO alternativas (id_alternativa, texto, correcto, id_pregunta) VALUES (10,'Un lenguaje de programación',true,10);
INSERT IGNORE INTO alternativas (id_alternativa, texto, correcto, id_pregunta) VALUES (20,'Un sistema operativo',false,10);
INSERT IGNORE INTO alternativas (id_alternativa, texto, correcto, id_pregunta) VALUES (30,'Un navegador web',false,10);