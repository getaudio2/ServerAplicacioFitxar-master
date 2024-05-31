DROP TABLE IF EXISTS usuarios;
CREATE TABLE usuarios (
  id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  nom VARCHAR(45) NOT NULL,
  contrasena VARCHAR(45) NOT NULL
);
DROP TABLE IF EXISTS presencias;
CREATE TABLE presencias (
  id int PRIMARY KEY  NOT NULL AUTO_INCREMENT,
  nom VARCHAR(45) NOT NULL,
  latitud double NOT NULL,
  longitud double NOT NULL,
  fecha datetime NOT NULL,
  distancia integer NOT NULL,
  comentario VARCHAR(45) NOT NULL
);

DROP TABLE IF EXISTS modulos;
CREATE TABLE IF NOT EXISTS modulos (
  id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  modulo VARCHAR(45) NULL,
  grupo VARCHAR(45) NULL,
  franjas_id int NOT NULL,
  dias_id int NOT NULL
  );