CREATE DATABASE dbTransporte;
USE dbTransporte;

CREATE TABLE buses (
  placa VARCHAR(7) PRIMARY KEY,
  conductor VARCHAR(40),
  empresa VARCHAR(40),
  aforo INTEGER,
  cantidadDePaquetes INTEGER
);

CREATE TABLE sedes (
  nombre VARCHAR(40) PRIMARY KEY,
  direccion VARCHAR(40)
);

CREATE TABLE viajes (
  idViaje INTEGER PRIMARY KEY AUTO_INCREMENT,
  cantidadCuposPasajeros INTEGER,
  cantidadCuposPaqueteria INTEGER,
  idBus VARCHAR(7),
  fechayHora TIMESTAMP,
  ciudadOrigen VARCHAR(40),
  ciudadDestino VARCHAR(40),
  cuposLlenosPasajeros INTEGER,
  cuposLlenosPaqueteria INTEGER,
  FOREIGN KEY (idBus) REFERENCES buses(placa),
  FOREIGN KEY (ciudadOrigen) REFERENCES sedes(nombre),
  FOREIGN KEY (ciudadDestino) REFERENCES sedes(nombre)
);

CREATE TABLE boletoOfacturaViajes (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  idViaje INTEGER,
  nombreUsuario VARCHAR(40),
  idUsuario BIGINT,
  equipaje BOOLEAN,
  cantidadEquipaje INTEGER,
  pesoEquipaje INTEGER,
  FOREIGN KEY (idViaje) REFERENCES viajes(idViaje)
);

CREATE TABLE facturaEnvios (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  idViaje INTEGER,
  nombreRemitente VARCHAR(40),
  idRemitente BIGINT,
  nombreDestinatario VARCHAR(40),
  idDestinatario BIGINT,
  pesoPaquete INTEGER,
  FOREIGN KEY (idViaje) REFERENCES viajes(idViaje)
);

CREATE TABLE pagosBoletos (
  idPago INTEGER PRIMARY KEY AUTO_INCREMENT,
  idBoleto INTEGER,
  metodoPago VARCHAR(40),
  numeroTarjeta VARCHAR(60),
  precio INTEGER,
  FOREIGN KEY (idBoleto) REFERENCES boletoOfacturaViajes(id)
);

CREATE TABLE pagosEnvios (
  idPago INTEGER PRIMARY KEY AUTO_INCREMENT,
  idFacturaEnvio INTEGER,
  metodoPago VARCHAR(40),
  numeroTarjeta VARCHAR(60),
  precio INTEGER,
  FOREIGN KEY (idFacturaEnvio) REFERENCES facturaEnvios(id)
);
