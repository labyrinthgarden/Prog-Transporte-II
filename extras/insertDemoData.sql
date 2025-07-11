-- Inserciones para poblar la base de datos dbTransporte

USE dbTransporte;

-- Sedes
INSERT INTO sedes (nombre, direccion) VALUES
  ('Bogotá', 'Calle 123 #45-67'),
  ('Medellín', 'Av. El Poblado 101'),
  ('Cali', 'Cra. 50 #10-20'),
  ('Barranquilla', 'Calle 30 #20-10');

-- Buses
INSERT INTO buses (placa, conductor, empresa, aforo, cantidadDePaquetes) VALUES
  ('ABC123', 'Juan Pérez', 'Expreso Colombia', 40, 20),
  ('XYZ789', 'Ana Gómez', 'Rápido Norte', 35, 15),
  ('LMN456', 'Carlos Ruiz', 'SurTrans', 50, 25);

-- Viajes
INSERT INTO viajes (cantidadCuposPasajeros, cantidadCuposPaqueteria, idBus, fechayHora, ciudadOrigen, ciudadDestino, cuposLlenosPasajeros, cuposLlenosPaqueteria) VALUES
  (40, 20, 'ABC123', '2025-07-15 08:00:00', 'Bogotá', 'Medellín', 10, 5),
  (35, 15, 'XYZ789', '2025-07-16 09:30:00', 'Medellín', 'Cali', 5, 2),
  (50, 25, 'LMN456', '2025-07-17 07:45:00', 'Cali', 'Barranquilla', 20, 10);

-- Boletos/Facturas de Viajes
INSERT INTO boletoOfacturaViajes (idViaje, nombreUsuario, idUsuario, equipaje, cantidadEquipaje, pesoEquipaje) VALUES
  (1, 'Pedro López', 1001, TRUE, 2, 15),
  (1, 'María Torres', 1002, FALSE, 0, 0),
  (2, 'Luis Martínez', 1003, TRUE, 1, 8);

-- Facturas de Envios
INSERT INTO facturaEnvios (idViaje, nombreRemitente, idRemitente, nombreDestinatario, idDestinatario, pesoPaquete) VALUES
  (1, 'Pedro López', 1001, 'Sofía Ramírez', 2001, 5),
  (2, 'Luis Martínez', 1003, 'Carlos Mendoza', 2002, 12),
  (3, 'Ana Gómez', 1004, 'Laura Jiménez', 2003, 7);

-- Pagos de Boletos
INSERT INTO pagosBoletos (idBoleto, metodoPago, numeroTarjeta, precio) VALUES
  (1, 'VISA', '4111111111111111', 50000),
  (2, 'Efectivo al llegar', '', 30000),
  (3, 'Mastercard', '5500000000000004', 45000);

-- Pagos de Envios
INSERT INTO pagosEnvios (idFacturaEnvio, metodoPago, numeroTarjeta, precio) VALUES
  (1, 'VISA', '4111111111111111', 15000),
  (2, 'Efectivo al llegar', '', 12000),
  (3, 'Mastercard', '5500000000000004', 18000);
