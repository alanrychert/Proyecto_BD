#Archivo batch para la creación de la 
#Base de datos del práctico de SQL

# Creo de la Base de Datos
CREATE DATABASE parquimetros;

# selecciono la base de datos sobre la cual voy a hacer modificaciones
USE parquimetros;

#-------------------------------------------------------------------------
# Creación Tablas para las entidades
CREATE TABLE conductores (
 dni SMALLINT (45) positive NOT NULL,
 nombre VARCHAR(45) NOT NULL,
 apellido VARCHAR(45) NOT NULL,
 direccion VARCHAR(45) NOT NULL,
 telefono VARCHAR(45) positive NOT NULL,
 registro SMALLINT positive NOT NULL,

 
 CONSTRAINT pk_conductores
 PRIMARY KEY (dni)
}ENGINE=InnoDB

CREATE TABLE automoviles (
 marca VARCHAR(45) NOT NULL, 
 modelo VARCHAR(45) NOT NULL,
 patente VARCHAR(6) NOT NULL, 
 color VARCHAR(45) NOT NULL,
 dni SMALLINT (45) positive NOT NULL;

 CONSTRAINT pk_automoviles
 PRIMARY KEY (patente),

 CONSTRAINT FK_automoviles
 FOREIGN KEY (dni) REFERENCES conductores(dni)
   ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE tipos_tarjeta (
 tipo VARCHAR (45),
 descuento DECIMAL (2,2) positive NOT NULL;

 CONSTRAINT tipos_tarjeta
 PRIMARY KEY (tipo),
) ENGINE=InnoDB;

CREATE TABLE tarjetas(
	id_tarjeta SMALLINT positive NOT NULL,
	saldo SMALLINT DECIMAL(5,2) NOT NULL,
	tipo VARCHAR(45) NOT NULL,
	patente VARCHAR(45) NOT NULL;

	CONSTRAINT pk_tarjetas
		PRIMARY KEY (id_tarjeta),


	CONSTRAINT FK_tarjetas
	FOREIGN KEY (patente) REFERENCES patente(patente)
   		ON DELETE RESTRICT ON UPDATE CASCADE

)ENGINE=InnoDB;

CREATE TABLE inspectores(
	legajo SMALLINT positive NOT NULL,
	dni SMALLINT positive NOT NULL,
	nombre VARCHAR(45) NOT NULL,
	password CHAR(32) NOT NULL,
	apellido VARCHAR(45) NOT NULL;


	CONSTRAINT inspectores
		PRIMARY KEY (legajo),
)ENGINE=InnoDB;

CREATE TABLE ubicaciones(
	altura SMALLINT positive NOT NULL,
	calle VARCHAR(45) NOT NULL,
	tarifa SMALLINT DECIMAL(5,2) NOT NULL;


	CONSTRAINT pk_ubicaciones
		PRIMARY KEY (altura,calle),
)ENGINE=InnoDB;


 CREATE USER 'ventas'@'%'  IDENTIFIED BY 'venta';
 GRANT INSERT ON venta TO 'admin_batallas'@'localhost' WITH GRANT OPTION;

#ELIMINAR EL USUARIO VACIO 


#----------------------------------------------------------alan

CREATE TABLE Multa (
 numero INT POSITIVE NOT NULL,
 fecha DATE NOT NULL,
 hora TIME NOT NULL,
 patente VARCHAR(6) NOT NULL, 
 id_asociado_con POSITIVE NOT NULL,
 
 CONSTRAINT pk_multa
 PRIMARY KEY (numero),

 CONSTRAINT FK_patente 
 FOREIGN KEY (patente) REFERENCES Automovil (patente) 
   ON DELETE RESTRICT ON UPDATE CASCADE,

 CONSTRAINT FK_asociado_con 
 FOREIGN KEY (id_asociado_con) REFERENCES Asociado_con (id_asociado_con) 
   ON DELETE RESTRICT ON UPDATE CASCADE,
) ENGINE=InnoDB;

CREATE TABLE Asociado_con (
 id_asociado_con INT POSITIVE NOT NULL,
 legajo INT POSITIVE NOT NULL,
 calle VARCHAR(45) NOT NULL,
 altura INT POSITIVE NOT NULL,
 dia VARCHAR(2) NOT NULL,
 turno VARCHAR(1) NOT NULL,

 
 CONSTRAINT pk_id_asociado_con
 PRIMARY KEY (id_asociado_con),

 CONSTRAINT fk_legajo
 FOREIGN KEY (legajo) REFERENCES Inspectores (legajo),
   ON DELETE RESTRICT ON UPDATE CASCADE,

 CONSTRAINT fk_calle
 FOREIGN KEY (calle) REFERENCES Ubicaciones (calle),
   ON DELETE RESTRICT ON UPDATE CASCADE,

 CONSTRAINT fk_altura
 FOREIGN KEY (altura) REFERENCES Ubicaciones (altura),
   ON DELETE RESTRICT ON UPDATE CASCADE,

) ENGINE=InnoDB;

CREATE TABLE Accede (
 legajo INT POSITIVE NOT NULL,
 id_parq INT POSITIVE NOT NULL,
 fecha DATE NOT NULL,
 hora TIME NOT NULL,

 
 CONSTRAINT pk_accede
 PRIMARY KEY (id_parq,fecha,hora),

 CONSTRAINT fk_id_parq
 FOREIGN KEY (id_parq) REFERENCES Parquimetros (id_parq),
   ON DELETE RESTRICT ON UPDATE CASCADE,

 CONSTRAINT fk_legajo
 FOREIGN KEY (legajo) REFERENCES Inspectores (legajo),
   ON DELETE RESTRICT ON UPDATE CASCADE,
) ENGINE=InnoDB;

CREATE TABLE Estacionamientos (
 id_tarjeta INT POSITIVE NOT NULL,
 id_parq INT POSITIVE NOT NULL,
 fecha_ent DATE NOT NULL,
 hora_ent TIME NOT NULL,
 fecha_sal DATE NOT NULL,
 hora_sal TIME NOT NULL,

 
 CONSTRAINT pk_Estacionamientos
 PRIMARY KEY (id_parq,fecha_ent,hora_ent),

 CONSTRAINT fk_id_parq
 FOREIGN KEY (id_parq) REFERENCES Parquimetros (id_parq),
   ON DELETE RESTRICT ON UPDATE CASCADE,

 CONSTRAINT fk_id_tarjeta
 FOREIGN KEY (id_tarjeta) REFERENCES Tarjeta (id_tarjeta),
   ON DELETE RESTRICT ON UPDATE CASCADE,
) ENGINE=InnoDB;

CREATE TABLE Parquimetros (
 id_parq INT POSITIVE NOT NULL,
 numero INT POSITIVE NOT NULL,
 calle VARCHAR(45) NOT NULL,
 altura INT POSITIVE NOT NULL,

 
 CONSTRAINT pk_Estacionamientos
 PRIMARY KEY (id_parq),

 CONSTRAINT fk_calle
 FOREIGN KEY (calle) REFERENCES Ubicaciones (calle),
   ON DELETE RESTRICT ON UPDATE CASCADE,

 CONSTRAINT fk_altura
 FOREIGN KEY (altura) REFERENCES Ubicaciones (altura),
   ON DELETE RESTRICT ON UPDATE CASCADE,
) ENGINE=InnoDB;

#-------------------------------------------------------------------------
# Creaci�n de vistas 
# acorazados = Datos de todos los barcos que son "acorazados"

   CREATE VIEW estacionados AS 
   SELECT b.nombre_barco, b.capitan, 
          c.clase, c.pais, c.nro_caniones, c.calibre, c. desplazamiento,
		  b_c.lanzado
   FROM (barcos as b JOIN  barco_clase as b_c ON b.nombre_barco = b_c.nombre_barco) 
        JOIN clases as c   ON c.clase = b_c.clase
   WHERE c.tipo="acorazado";

#-------------------------------------------------------------------------


#-------------------------------------------------------------------------
# Creaci�n de usuarios 

# primero creo un usuario con CREATE USER
 
   CREATE USER 'admin'@'localhost'  IDENTIFIED BY 'admin';

# el usuario admin_batallas con password 'admin' puede conectarse solo 
# desde la desde la computadora donde se encuentra el servidor de MySQL (localhost)   

# luego le otorgo privilegios utilizando solo la sentencia GRANT

    GRANT ALL PRIVILEGES ON parquimetros.* TO 'admin'@'localhost' WITH GRANT OPTION;
