INSERT INTO usuarios (id,nom, contrasena) VALUES
('1', 'admin', 'admin');

INSERT INTO presencias (id, nom, latitud,longitud,fecha, distancia,comentario) VALUES
('1', 'edu', 41.33434, 2.222, '2022-08-16 23:12:12', 10, 'Ha fichado correctamente'),
('2', 'jesus', 41.33434, 2.222, '2022-08-17 12:12:12',20000 ,'No ha fichado correctamente'),
('3', 'edu', 41.33434, 2.222, '2022-08-17 00:00:00',2000 ,'No ha fichado correctamente'),
('4', 'jesus', 41.33434, 2.222, '2022-08-16 12:12:12',6 ,'Ha fichado correctamente'),
('5', 'jesus', 41.33434, 2.222, '2022-08-15 00:00:00',13000 ,'No ha fichado correctamente');

INSERT INTO modulos (id, modulo, grupo, franjas_id, dias_id) VALUES
('1','PROJECTE','DAM2',7,1),
('2','PROJECTE','DAM2',8,1),
('3','CFTUT','DAM2',9,1),
('4','DAMM8','DAM2',10,1),
('5','DAMM8','DAM2',11,1),
('6','DAMM6','DAM2',12,1),
('7','PROJECTE','DAM2',7,2),
('8','PROJECTE','DAM2',8,2),
('9','DAMM6','DAM2',9,2),
('10','DAMM6','DAM2',10,2),
('11','DAMM9','DAM2',11,2),
('12','DAMM9','DAM2',12,2),
('13','PROJECTE','DAM2',7,3),
('14','PROJECTE','DAM2',8,3),
('15','BIGDATA','DAM2',9,3),
('16','BIGDATA','DAM2',10,3),
('17','DAMM7','DAM2',11,3),
('18','DAMM8','DAM2',8,4),
('19','DAMM8','DAM2',9,4),
('20','DAMM7','DAM2',10,4),
('21','DAMM7','DAM2',11,4),
('22','PROJECTE','DAM2',7,5),
('23','PROJECTE','DAM2',8,5),
('24','PROJECTE','DAM2',9,5);



