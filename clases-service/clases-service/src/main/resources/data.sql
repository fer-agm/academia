INSERT IGNORE INTO categoria (id_categoria, nombre_categoria, descripcion_categoria) VALUES (1,'Programación','Se enseña a programar');
INSERT IGNORE INTO categoria (id_categoria, nombre_categoria, descripcion_categoria) VALUES (2,'Bordado','Se enseña a bordar');
INSERT IGNORE INTO categoria (id_categoria, nombre_categoria, descripcion_categoria) VALUES (3,'Cocina','Se enseña a cocinar');
INSERT IGNORE INTO categoria (id_categoria, nombre_categoria, descripcion_categoria) VALUES (4,'Arte','Se enseña arte');

INSERT IGNORE INTO curso (id_curso, nombre_curso, descripcion_curso, valor_curso, id_categoria) VALUES (10,'Python 1','Curso de Python básico',150000,1);
INSERT IGNORE INTO curso (id_curso, nombre_curso, descripcion_curso, valor_curso, id_categoria) VALUES (20,'Python 2','Curso de Python intermedio',150000,1);
INSERT IGNORE INTO curso (id_curso, nombre_curso, descripcion_curso, valor_curso, id_categoria) VALUES (30,'Python 3','Curso de Python avanzado',150000,1);
INSERT IGNORE INTO curso (id_curso, nombre_curso, descripcion_curso, valor_curso, id_categoria) VALUES (40,'Java 1','Curso de Java básico',150000,1);
INSERT IGNORE INTO curso (id_curso, nombre_curso, descripcion_curso, valor_curso, id_categoria) VALUES (50,'Java 2','Curso de Java intermedio',150000,1);
INSERT IGNORE INTO curso (id_curso, nombre_curso, descripcion_curso, valor_curso, id_categoria) VALUES (60,'Java 3','Curso de Java avanzado',150000,1);
INSERT IGNORE INTO curso (id_curso, nombre_curso, descripcion_curso, valor_curso, id_categoria) VALUES (70,'Punto Cruz','Curso de punto cruz',150000,2);
INSERT IGNORE INTO curso (id_curso, nombre_curso, descripcion_curso, valor_curso, id_categoria) VALUES (80,'Pizzas','Curso de pizzas',70000,3);
INSERT IGNORE INTO curso (id_curso, nombre_curso, descripcion_curso, valor_curso, id_categoria) VALUES (90,'Sushi','Curso de sushi',70000,3);
INSERT IGNORE INTO curso (id_curso, nombre_curso, descripcion_curso, valor_curso, id_categoria) VALUES (100,'Pintura','Curso de pintura',150000,4);

INSERT IGNORE INTO clase (id_clase, nombre_clase, contenido_clase, duracion_clase, id_curso) VALUES (10,'Clase 1','Introducción',60,10);
INSERT IGNORE INTO clase (id_clase, nombre_clase, contenido_clase, duracion_clase, id_curso) VALUES (20,'Clase 1','Introducción',60,20);
INSERT IGNORE INTO clase (id_clase, nombre_clase, contenido_clase, duracion_clase, id_curso) VALUES (30,'Clase 1','Introducción',60,30);