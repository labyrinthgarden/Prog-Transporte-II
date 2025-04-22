package com.servidor.repository;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class eliminarRegistrosRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public eliminarRegistrosRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int eliminarSede(String nombre) {
        String query = "DELETE FROM sedes WHERE nombre = ?";
        return jdbcTemplate.update(query, nombre);
    }
    public int eliminarBus(int id) {
        String query = "DELETE FROM buses WHERE idBus = ?";
        return jdbcTemplate.update(query, id);
    }
    public int eliminarViaje(int id) {
        String query = "DELETE FROM viajes WHERE idViaje = ?";
        return jdbcTemplate.update(query, id);
    }
    public int eliminarPaquete(int id) {
        String query = "DELETE FROM facturaEnvios WHERE id = ?";
        return jdbcTemplate.update(query, id);
    }
}
