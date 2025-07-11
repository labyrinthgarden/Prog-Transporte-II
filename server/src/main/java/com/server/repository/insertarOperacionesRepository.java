package com.server.repository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class insertarOperacionesRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public insertarOperacionesRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int insertarViaje(String bus, String fechaYhora, String origen, String destino) {
        String busQuery = "SELECT aforo, cantidadDePaquetes FROM buses WHERE placa = ?";
        Map<String, Object> busDatos = jdbcTemplate.queryForMap(busQuery, bus);
        int aforo = (int) busDatos.get("aforo");
        int paquetes = (int) busDatos.get("cantidadDePaquetes");
        String insertQuery = "INSERT INTO viajes (cantidadCuposPasajeros, cantidadCuposPaqueteria, idBus, fechayHora, ciudadOrigen, ciudadDestino, cuposLlenosPasajeros, cuposLlenosPaqueteria) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(insertQuery, aforo, paquetes, bus, fechaYhora, origen, destino, 0, 0);
    }
    public int insertarSede(String nombre, String direccion) {
        String query = "INSERT INTO sedes (nombre,direccion) VALUES (?,?)";
        return jdbcTemplate.update(query, nombre, direccion);
    }
    public int insertarBus(String conductor,String empresa,String placa,int aforo,int cantidadDePaquetes) {
        String query = "INSERT INTO buses (conductor,empresa,placa,aforo,cantidadDePaquetes) VALUES (?,?,?,?,?)";
        return jdbcTemplate.update(query,conductor,empresa,placa,aforo,cantidadDePaquetes);
    }

    public int reservarViaje(Object datos, Object infoPago) {
        List<?> datosList = (List<?>) datos;
        List<?> infoPagoList = (List<?>) infoPago;

        String query0 = "SELECT idViaje FROM viajes WHERE fechaYHora = ? AND ciudadOrigen = ? AND ciudadDestino = ? AND cantidadCuposPasajeros > cuposLlenosPasajeros";
        Object nombreUsuario = datosList.get(0);
        Object idUsuario = datosList.get(1);
        Object origen = datosList.get(2);
        Object destino = datosList.get(3);
        Object fecha = datosList.get(4);
        Object equipaje = datosList.get(5);

        Integer idViaje = jdbcTemplate.queryForObject(query0, Integer.class, fecha, origen, destino);

        final int cantidadEquipajeValue;
        Object cantidadEquipaje = datosList.get(6);
        if (cantidadEquipaje != null && !cantidadEquipaje.toString().trim().isEmpty()) {
            cantidadEquipajeValue = Integer.parseInt(cantidadEquipaje.toString());
        } else {
            cantidadEquipajeValue = 0;
        }

        final int pesoEquipajeValue;
        Object pesoEquipaje = datosList.get(7);
        if (pesoEquipaje != null && !pesoEquipaje.toString().trim().isEmpty()) {
            pesoEquipajeValue = Integer.parseInt(pesoEquipaje.toString());
        } else {
            pesoEquipajeValue = 0;
        }

        String query1 = "INSERT INTO boletoOfacturaViajes (idViaje, nombreUsuario, idUsuario, equipaje, cantidadEquipaje, pesoEquipaje) VALUES (?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, idViaje);
            ps.setObject(2, nombreUsuario);
            ps.setObject(3, idUsuario);
            ps.setObject(4, equipaje);
            ps.setInt(5, cantidadEquipajeValue);
            ps.setInt(6, pesoEquipajeValue);
            return ps;
        }, keyHolder);

        int idBoleto = keyHolder.getKey().intValue();
        String metodoPago = (String) infoPagoList.get(0);
        String numeroTarjeta = (String) infoPagoList.get(1);
        int precio = Integer.parseInt(infoPagoList.get(2).toString());

        String query2 = "INSERT INTO pagosBoletos (idBoleto, metodoPago, numeroTarjeta, precio) VALUES (?,?,?,?)";
        jdbcTemplate.update(query2, idBoleto, metodoPago, numeroTarjeta, precio);

        String updateCupos = "UPDATE viajes SET cuposLlenosPasajeros = cuposLlenosPasajeros + 1 WHERE idViaje = ?";
        return jdbcTemplate.update(updateCupos, idViaje);
    }
    public int reservarEnvio(Object datos, Object infoPago) {
        List<?> datosList = (List<?>) datos;
        List<?> infoPagoList = (List<?>) infoPago;
        Object origen = datosList.get(4);
        Object destino = datosList.get(5);
        String query0 = "SELECT idViaje FROM viajes WHERE ciudadOrigen = ? AND ciudadDestino = ? AND cantidadCuposPaqueteria > cuposLlenosPaqueteria";
        Integer idViaje = jdbcTemplate.queryForObject(query0, Integer.class, origen, destino);

        Object nombreRemitente = datosList.get(0);
        Object idRemitente = datosList.get(1);
        Object nombreDestinatario = datosList.get(2);
        Object idDestinatario = datosList.get(3);
        Object pesoPaquete = datosList.get(6);
        String query1 = "INSERT INTO facturaEnvios (idViaje, nombreRemitente, idRemitente, nombreDestinatario, idDestinatario, pesoPaquete) VALUES (?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, idViaje);
            ps.setObject(2, nombreRemitente);
            ps.setObject(3, idRemitente);
            ps.setObject(4, nombreDestinatario);
            ps.setObject(5, idDestinatario);
            ps.setObject(6, pesoPaquete);
            return ps;
        }, keyHolder);

        int id = keyHolder.getKey().intValue();

        String metodoPago = (String) infoPagoList.get(0);
        String numeroTarjeta = (String) infoPagoList.get(1);
        int precio = Integer.parseInt(infoPagoList.get(2).toString());

        String query2 = "INSERT INTO pagosEnvios (idFacturaEnvio, metodoPago, numeroTarjeta, precio) VALUES (?,?,?,?)";
        jdbcTemplate.update(query2, id, metodoPago, numeroTarjeta, precio);

        String updateCupos = "UPDATE viajes SET cuposLlenosPaqueteria = cuposLlenosPaqueteria + 1 WHERE idViaje = ?";
        return jdbcTemplate.update(updateCupos, idViaje);
    }

}
