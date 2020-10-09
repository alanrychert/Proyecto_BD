USE batallas;

#Inserciones de inspectores (el legajo es de 1 solo digito, el dni de 2 digitos)
#-------------------------------------------------------------------
INSERT INTO inspectores VALUES(1,11,"Inspector1","uno","Ap1_I");
INSERT INTO inspectores VALUES(2,22,"Inspector2","dos","Ap2_I");
INSERT INTO inspectores VALUES(3,33,"Inspector3","tres","Ap3_I");
INSERT INTO inspectores VALUES(4,44,"Inspector4","cuatro","Ap4_I");
INSERT INTO inspectores VALUES(5,55,"Inspector5","cinco","Ap5_I");
#--------------------------------------------------------------------

#Inserciones de conductores(el dni es de 3 digitos, el nro de telefono de 4 digitos, y
#el registro de 1 digito)
#------------------------------Dni,nombre,  apellido, direccion, telefono, registro
#--------------------------------------------------------------------
INSERT INTO conductores VALUES(111,"Conductor1","Ap1_C","dir1",1111,1);
INSERT INTO conductores VALUES(222,"Conductor2","Ap2_C","dir2",2222,2);
INSERT INTO conductores VALUES(333,"Conductor3","Ap3_C","dir3",3333,3);
INSERT INTO conductores VALUES(444,"Conductor4","Ap4_C","dir4",4444,4);
INSERT INTO conductores VALUES(555,"Conductor5","Ap5_C","dir5",5555,5);
#--------------------------------------------------------------------

#Inserciones de automoviles(el dni de los dueños (conductores) es de 3 digitos)
#------------------------------marca,  modelo   patente, color, dni dueño
#--------------------------------------------------------------------
INSERT INTO automoviles VALUES("Ford","Falcon","AAA111","rojo",111);
INSERT INTO automoviles VALUES("Peugeot","306","AAA222","azul",111);
INSERT INTO automoviles VALUES("Ford","Ka","AAA333","gris",222);
INSERT INTO automoviles VALUES("Peugeot","408","AAA444","negro",333);
INSERT INTO automoviles VALUES("Ford","Fiesta","AAA555","blanco",444);
#---------------------------------------------------------------------

#Inserciones de ubicaciones (las alturas son de 3 digitos y terminan en 00)
#------------------------------altura, calle, tarifa
#---------------------------------------------------------------------
INSERT INTO ubicaciones VALUES(100,"calle1",1.0);
INSERT INTO ubicaciones VALUES(200,"calle2",2.0);
INSERT INTO ubicaciones VALUES(300,"calle3",3.0);
INSERT INTO ubicaciones VALUES(400,"calle4",4.0);
INSERT INTO ubicaciones VALUES(500,"calle5",5.0);
#---------------------------------------------------------------------


#Inserciones de asociado con (el id_asociado_con es de 1 digito)
#---------------------id_asociado_con,legajo de inspector,calle,altura,dia,turno
#---------------------------------------------------------------------
INSERT INTO asociado_con VALUES(1,1,"calle1",100,"lu","m");
INSERT INTO asociado_con VALUES(2,1,"calle2",200,"ma","m");
INSERT INTO asociado_con VALUES(3,1,"calle3",300,"mi","t");
INSERT INTO asociado_con VALUES(4,1,"calle4",400,"ju","t");
INSERT INTO asociado_con VALUES(5,1,"calle5",500,"vi","t");
#---------------------------------------------------------------------

#Inserciones de multa (el nro de multa tiene 1 digito )
#---------------------nro de multa,fecha, hora, patente, id_asociado_con
#----------------------------------------------------------------------
INSERT INTO multa VALUES(1,"2020/01/01","01:11:11","AAA111",1);
INSERT INTO multa VALUES(2,"2020/01/01","02:22:22","AAA111",2);
INSERT INTO multa VALUES(3,"2020/01/01","03:11:11","AAA333",3);
INSERT INTO multa VALUES(4,"2020/01/01","04:11:11","AAA444",4);
INSERT INTO multa VALUES(5,"2020/01/01","05:11:11","AAA555",4);
#----------------------------------------------------------------------
