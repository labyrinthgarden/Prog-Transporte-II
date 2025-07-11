package com.server.repository;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class listarDatosRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public listarDatosRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> listarOpcionesBuses() {
        String query = "SELECT placa FROM buses";
        return jdbcTemplate.queryForList(query,String.class);
    }
    public List<String> listarOpcionesSedes() {
        String query = "SELECT nombre FROM sedes";
        return jdbcTemplate.queryForList(query,String.class);
    }

    public List<Map<String, Object>> listarSedes() {
        String query = "SELECT * FROM sedes";
        return jdbcTemplate.queryForList(query);
    }
    public List<Map<String, Object>> listarViajes() {
        String query = "SELECT * FROM viajes";
        return jdbcTemplate.queryForList(query);
    }
    public List<Map<String, Object>> listarBuses() {
        String query = "SELECT * FROM buses";
        return jdbcTemplate.queryForList(query);
    }
    public List<String> listarConductores() {
        String query = "SELECT conductor FROM buses";
        return jdbcTemplate.queryForList(query,String.class);
    }
    public List<Map<String, Object>> listarPagos() {
        String query = "SELECT pagosBoletos.*, boletoOfacturaViajes.nombreUsuario FROM pagosBoletos INNER JOIN boletoOfacturaViajes ON pagosBoletos.idPago=boletoOfacturaViajes.id;";
        return jdbcTemplate.queryForList(query);
    }
    public List<Map<String, Object>> listarPagosEnvios() {
        String query = "SELECT pagosEnvios.*, facturaEnvios.nombreRemitente FROM pagosEnvios INNER JOIN facturaEnvios ON pagosEnvios.idPago=facturaEnvios.id;";
        return jdbcTemplate.queryForList(query);
    }
    public List<Map<String, Object>> listarPaqueteria() {
        String query = "SELECT * FROM facturaEnvios";
        return jdbcTemplate.queryForList(query);
    }

    public List<String> listarFechasDisponibles(String origen, String destino) {
        String query = "SELECT fechaYHora FROM viajes WHERE ciudadOrigen = ? AND ciudadDestino = ?";
        List<String> resultado = jdbcTemplate.queryForList(query, String.class, origen, destino);
        if (resultado.isEmpty()) {
            return List.of("No tenemos viajes disponibles entre estas ubicaciones");
        } else {
            return resultado;
        }
    }
}
