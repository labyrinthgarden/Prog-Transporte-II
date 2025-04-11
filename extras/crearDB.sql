CREATE TABLE viajes (
  idViaje INTEGER PRIMARY KEY,
  cantidadCuposLlenosPasajeros INTEGER,
  cantidadCuposLlenosPaqueteria INTEGER,
  equipaje BOOLEAN,
  idBus INTEGER,
  fechayHora TIMESTAMP,
  ciudadOrigen INTEGER,
  ciudadDestino INTEGER,
  cuposLlenosPasajeros BOOLEAN,
  cuposLlenosPaqueteria BOOLEAN,
  FOREIGN KEY (idBus) REFERENCES buses(idBus),
  FOREIGN KEY (ciudadOrigen) REFERENCES sedes(idSede),
  FOREIGN KEY (ciudadDestino) REFERENCES sedes(idSede)
);
CREATE TABLE boletosOfacturaViajes (
  idViaje INTEGER,
  nombreUsuario VARCHAR(255),
  idUsuario INTEGER,
  equipaje BOOLEAN,
  cantidadEquipaje INTEGER,
  pesoEquipaje INTEGER,
  precio INTEGER,
  FOREIGN KEY (idViaje) REFERENCES viajes(idViaje)
);
CREATE TABLE facturaEnvios (
  idEnvio INTEGER,
  idViaje INTEGER,
  nombreRemitente VARCHAR(255),
  idRemitente INTEGER,
  nombreDestinatario VARCHAR(255),
  idDestinatario INTEGER,
  pesoPaquete INTEGER,
  precio INTEGER,
  FOREIGN KEY (idViaje) REFERENCES viajes(idViaje)
);
CREATE TABLE buses (
  idBus INTEGER PRIMARY KEY,
  conductor VARCHAR(255),
  empresa VARCHAR(255),
  aforo INTEGER,
  cantidadDePaquetes INTEGER
);
CREATE TABLE sedes (
  idSede INTEGER PRIMARY KEY,
  nombre VARCHAR(255)
);
