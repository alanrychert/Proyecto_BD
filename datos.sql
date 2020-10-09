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
#--------------------------------------------------------------------
INSERT INTO conductores VALUES(111,"Conductor1","Ap1_C","dir1",1111,1);




INSERT INTO Tipos_tarjeta VALUES("tipo1",10.00);
INSERT INTO Tipos_tarjeta VALUES("tipo2",20.00);
INSERT INTO Tipos_tarjeta VALUES("tipo3",30.00);
INSERT INTO Tipos_tarjeta VALUES("tipo4",40.00);
INSERT INTO Tipos_tarjeta VALUES("tipo5",50.00);

#Inserciones tarjetas(el id de tarjeta es de 9 digitos )
INSERT INTO Tarjetas VALUES(111111111,0.0,"tipo1","AAA111");
INSERT INTO Tarjetas VALUES(122222222,0.0,"tipo2","AAA222");
INSERT INTO Tarjetas VALUES(333333333,0.0,"tipo2","AAA333");
INSERT INTO Tarjetas VALUES(444444444,0.0,"tipo2","AAA444");
INSERT INTO Tarjetas VALUES(555555555,0.0,"tipo5","AAA555");

#Inserciones parquimetros( el id de parquimetro tendra 10 digitos)

INSERT INTO Parquimetros VALUES(1111111111,111,"calle1","100");
INSERT INTO Parquimetros VALUES(2222222222,222,"calle2","200");
INSERT INTO Parquimetros VALUES(3333333333,333,"calle3","300");
INSERT INTO Parquimetros VALUES(4444444444,444,"calle4","400");
INSERT INTO Parquimetros VALUES(5555555555,555,"calle5","500");

#Inserciones Estacionamientos

INSERT INTO Estacionamientos VALUES(111111111,1111111111,"2020/01/01","01:01:01","11:01:01");
INSERT INTO Estacionamientos VALUES(111111111,1111111111,"2020/01/01","01:01:01","11:01:01");
INSERT INTO Estacionamientos VALUES(333333333,3333333333,"2020/03/03","03:03:03","04:03:03");
INSERT INTO Estacionamientos VALUES(333333333,3333333333,"2020/03/03","03:03:03","04:03:03");
INSERT INTO Estacionamientos VALUES(555555555,5555555555,"2020/05/05","05:05:05","09:05:05");
