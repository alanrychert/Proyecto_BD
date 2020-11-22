#Archivo batch para la creacion de la 
#Base de datos del práctico de SQL

# Creo de la Base de Datos
CREATE DATABASE parquimetros;

# selecciono la base de datos sobre la cual voy a hacer modificaciones
USE parquimetros;

#-------------------------------------------------------------------------
#Creacion Tablas para las entidades

CREATE TABLE Ubicaciones(
	altura INT UNSIGNED NOT NULL,
	calle VARCHAR(45) NOT NULL,
	tarifa DECIMAL(5,2) UNSIGNED NOT NULL,


	CONSTRAINT pk_ubicaciones
		PRIMARY KEY (altura,calle)
)ENGINE=InnoDB;

CREATE TABLE Parquimetros(
 id_parq INT UNSIGNED NOT NULL,
 numero INT UNSIGNED NOT NULL,
 calle VARCHAR(45) NOT NULL,
 altura INT UNSIGNED NOT NULL,

 
 CONSTRAINT pk_parquimetros
 PRIMARY KEY (id_parq),

 CONSTRAINT fk_ubicacion_parquimetros
 FOREIGN KEY (altura,calle) REFERENCES Ubicaciones (altura,calle)
   ON DELETE RESTRICT ON UPDATE CASCADE

) ENGINE=InnoDB;

CREATE TABLE Inspectores(
	legajo INT UNSIGNED NOT NULL,
	dni INT UNSIGNED NOT NULL,
	nombre VARCHAR(45) NOT NULL,
	password CHAR(32) NOT NULL,
	apellido VARCHAR(45) NOT NULL,


	CONSTRAINT pk_inspectores
		PRIMARY KEY (legajo)
)ENGINE=InnoDB;

CREATE TABLE Asociado_con (
 id_asociado_con INT UNSIGNED NOT NULL AUTO_INCREMENT,
 legajo INT UNSIGNED NOT NULL,
 calle VARCHAR(45) NOT NULL,
 altura INT UNSIGNED NOT NULL,
 dia enum('do','lu','ma','mi','ju','vi','sa') NOT NULL,
 turno enum('m','t') NOT NULL,

 
 CONSTRAINT pk_id_asociado_con
 PRIMARY KEY (id_asociado_con),

 CONSTRAINT fk_legajo_asoc_con
 FOREIGN KEY (legajo) REFERENCES Inspectores(legajo)
   ON DELETE RESTRICT ON UPDATE CASCADE,

 CONSTRAINT fk_ubicacion
 FOREIGN KEY (altura,calle) REFERENCES Ubicaciones(altura,calle)
   ON DELETE RESTRICT ON UPDATE CASCADE

) ENGINE=InnoDB;

CREATE TABLE Conductores (
 dni INT (45) UNSIGNED NOT NULL,
 nombre VARCHAR(45) NOT NULL,
 apellido VARCHAR(45) NOT NULL,
 direccion VARCHAR(45) NOT NULL,
 telefono VARCHAR(45) ,
 registro INT(45) UNSIGNED NOT NULL,

 
 CONSTRAINT pk_conductores
 PRIMARY KEY (dni)
) ENGINE=InnoDB;

CREATE TABLE Automoviles (
 marca VARCHAR(45) NOT NULL, 
 modelo VARCHAR(45) NOT NULL,
 patente VARCHAR(6) NOT NULL, 
 color VARCHAR(45) NOT NULL,
 dni INT (45) UNSIGNED NOT NULL,

 CONSTRAINT pk_automoviles
 PRIMARY KEY (patente),

 CONSTRAINT FK_automoviles
 FOREIGN KEY (dni) REFERENCES Conductores(dni)
   ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Tipos_tarjeta (
 tipo VARCHAR (45) NOT NULL,
 descuento DECIMAL (3,2) UNSIGNED NOT NULL,

 CONSTRAINT tipos_tarjeta
 PRIMARY KEY (tipo)
) ENGINE=InnoDB;

CREATE TABLE Tarjetas(
	id_tarjeta INT UNSIGNED NOT NULL AUTO_INCREMENT,
	saldo DECIMAL(5,2) NOT NULL,
	tipo VARCHAR(45) NOT NULL,
	patente CHAR(6) NOT NULL,

	CONSTRAINT pk_tarjeta
		PRIMARY KEY (id_tarjeta),

	CONSTRAINT FK_tipo
	FOREIGN KEY (tipo) REFERENCES Tipos_tarjeta(tipo)
		ON DELETE RESTRICT ON UPDATE CASCADE,

	CONSTRAINT FK_tarjeta
	FOREIGN KEY (patente) REFERENCES Automoviles(patente)
   		ON DELETE RESTRICT ON UPDATE CASCADE

)ENGINE=InnoDB;


#----------------------------------------------------------alan

CREATE TABLE Multa (
 numero INT UNSIGNED NOT NULL AUTO_INCREMENT,
 fecha DATE NOT NULL,
 hora TIME NOT NULL,
 patente VARCHAR(6) NOT NULL, 
 id_asociado_con INT UNSIGNED NOT NULL,
 
 CONSTRAINT pk_multa
 PRIMARY KEY (numero),

 CONSTRAINT FK_patente 
 FOREIGN KEY (patente) REFERENCES Automoviles (patente) 
   ON DELETE RESTRICT ON UPDATE CASCADE,

 CONSTRAINT FK_asociado_con 
 FOREIGN KEY (id_asociado_con) REFERENCES Asociado_con (id_asociado_con) 
   ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;


CREATE TABLE Accede (
 legajo INT UNSIGNED NOT NULL,
 id_parq INT UNSIGNED NOT NULL,
 fecha DATE NOT NULL,
 hora TIME NOT NULL,

 
 CONSTRAINT pk_accede
 PRIMARY KEY (id_parq,fecha,hora),

 CONSTRAINT fk_id_parq
 FOREIGN KEY (id_parq) REFERENCES Parquimetros(id_parq)
   ON DELETE RESTRICT ON UPDATE CASCADE,

 CONSTRAINT fk_legajo
 FOREIGN KEY (legajo) REFERENCES Inspectores (legajo)
   ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Estacionamientos (
 id_tarjeta INT UNSIGNED NOT NULL ,
 id_parq INT UNSIGNED NOT NULL,
 fecha_ent DATE NOT NULL,
 hora_ent TIME NOT NULL,
 fecha_sal DATE,
 hora_sal TIME,

 
 CONSTRAINT pk_Estacionamientos
 PRIMARY KEY (id_parq,fecha_ent,hora_ent),

 CONSTRAINT fk_id_estacionamientos
 FOREIGN KEY (id_parq) REFERENCES Parquimetros(id_parq)
   ON DELETE RESTRICT ON UPDATE CASCADE,

 CONSTRAINT fk_id_tarjeta
 FOREIGN KEY (id_tarjeta) REFERENCES Tarjetas(id_tarjeta)
   ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

#-------------------------------------------------------------------------
# Creacion de vistas 
# estacionados = Permite ver la calle, altura y las patentes de los autos que tienen un estacionamiento "abierto" en cada ubicación

 CREATE VIEW estacionados AS 
 SELECT calle, altura, patente 
 FROM (parquimetros.parquimetros NATURAL JOIN parquimetros.estacionamientos NATURAL JOIN parquimetros.tarjetas)
 WHERE estacionamientos.fecha_sal is NULL and estacionamientos.hora_sal is NULL;

#-------------------------------------------------------------------------
delimiter !
create procedure conectar(IN tarjeta INTEGER, IN parquimetro INTEGER)
begin  
    declare nuevo_saldo decimal(5,2);
    declare saldo_actual decimal(5,2);
    declare fecha_act DATE;
    declare hora_act TIME;
    declare fecha_entrada DATE;
    declare hora_entrada TIME;
    declare diferencia_tiempo TIME;
    declare costo_minuto int;
    declare descontar DECIMAL(3,2);   

    
    declare estacionamientoAbierto boolean default false;

    declare C cursor for select * from estacionamientos where id_tarjeta = tarjeta and id_parq = parquimetro and (fecha_sal is NULL) and (hora_sal is NULL);
    
    declare continue handler for not found set estacionamientoAbierto = true;
    
    start transaction;



    SELECT saldo into saldo_actual from tarjetas where id_tarjeta=tarjeta; 
    SELECT tarifa into costo_minuto from parquimetros natural join ubicaciones where id_parq = parquimetro;
    SELECT descuento into descontar from tarjetas natural join tipos_tarjeta where id_tarjeta=tarjeta;
         
    
    if not(estacionamientoAbierto) then 
         SELECT CURRENT_DATE() INTO fecha_act;
         SELECT CURRENT_TIME() INTO hora_act;

         update estacionamientos set fecha_sal = fecha_act,hora_sal = hora_act where id_tarjeta = tarjeta and id_parq=parquimetro and fecha_sal is NULL and hora_sal is NULL;
         
         SELECT fecha_ent into fecha_entrada from estacionamientos where id_tarjeta = tarjeta and id_parq = parquimetro and fecha_sal = fecha_act and hora_sal = hora_act;
         SELECT hora_ent into hora_entrada from estacionamientos where id_tarjeta = tarjeta and id_parq = parquimetro and fecha_sal = fecha_act and hora_sal = hora_act;
         
         SELECT TIMEDIFF( TIMESTAMP (fecha_act, hora_act), TIMESTAMP (fecha_entrada, hora_entrada)) into diferencia_tiempo;

         select greatest(-999.99,(saldo_actual-((time_to_sec(diferencia_tiempo)/60) * costo_minuto * (1 - descontar)))) into nuevo_saldo;
         
         update tarjetas set saldo = nuevo_saldo where id_tarjeta = tarjeta; 
         
        select "cierre" as operacion, time_to_sec(diferencia_tiempo)/60 as tiempo_transcurrido, greatest(-999.99,nuevo_saldo) as saldo_actualizado;
        
    else
        
        if saldo_actual <0 
            then 
                select "apertura" as operacion, "no_exitosa" as resultado, saldo_actual/(costo_minuto*(1-descontar)) as tiempo_disponible;
            else
                INSERT INTO Estacionamientos VALUES(tarjeta,parquimetro,CURRENT_DATE(),CURRENT_TIME(),null,null);
                select "apertura" as operacion, "exitosa" as resultado, saldo_actual/(costo_minuto*(1-descontar)) as tiempo_disponible;
                
        end if;
    end if;
    
    end; !
delimiter ;

 


#-------------------------------------------------------------------------
# Creacion de usuarios 

# Creo el usuario admin con contraseña "admin" que únicamente se puede conectar desde la computadora donde
# se encuentra el servidor de MySQL
 
 CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin'; 

# luego le otorgo todos los privilegios sobre la base de datos y también el privilegio de 
# otorgar privilegios a otro usuario de la misma

 GRANT ALL PRIVILEGES ON parquimetros.* TO 'admin'@'localhost' WITH GRANT OPTION;

# Creo el usuario venta, que se puede conectar desde cualquier computadora

 CREATE USER 'venta'@'%' IDENTIFIED BY 'venta';

# Le otorgo permisos de inserción en la tabla tarjetas y de selección en la tabla tipos_tarjeta

 GRANT INSERT ON parquimetros.tarjetas TO 'venta'@'%';
 GRANT SELECT ON parquimetros.tipos_tarjeta TO 'venta'@'%';

# Creo el usuario inspector, con la constraseña "inspector" que se puede conectar desde cualquier computadora

CREATE USER 'inspector'@'%' IDENTIFIED BY 'inspector';

# Le otorgo los permisos necesarios para validar número de legajo y password de un inspector,
# Dado un parquímetro, obtener las patentes de los autom´oviles con un estacionamiento
# abierto en la ubicación del parqu´ımetro,
# cargar multas, y
# registrar accesos a parquímetros.
CREATE USER 'parquimetro'@'%' IDENTIFIED BY 'parquimetro'; 

GRANT execute on procedure parquimetros.conectar TO 'parquimetro'@'%';

GRANT SELECT ON parquimetros.Parquimetros TO 'inspector'@'%';
GRANT SELECT ON parquimetros.Multa TO 'inspector'@'%';
GRANT SELECT ON parquimetros.Inspectores TO 'inspector'@'%';
GRANT SELECT ON parquimetros.Estacionados TO 'inspector'@'%';
GRANT SELECT ON parquimetros.Asociado_con TO 'inspector'@'%';
GRANT INSERT ON parquimetros.Multa TO 'inspector'@'%';
GRANT INSERT ON parquimetros.Accede TO 'inspector'@'%';
