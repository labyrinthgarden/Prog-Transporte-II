package com.front.app;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClassViaje {
    @JsonProperty("idViaje")
    private int idViaje;
    @JsonProperty("cantidadCuposPasajeros")
    private int cantidadCuposPasajeros;
    @JsonProperty("cantidadCuposPaqueteria")
    private int cantidadCuposPaqueteria;
    @JsonProperty("idBus")
    private String idBus;
    @JsonProperty("fechayHora")
    private String fechayHora;
    @JsonProperty("ciudadOrigen")
    private String ciudadOrigen;
    @JsonProperty("ciudadDestino")
    private String ciudadDestino;
    @JsonProperty("cuposLlenosPasajeros")
    private int cuposLlenosPasajeros;
    @JsonProperty("cuposLlenosPaqueteria")
    private int cuposLlenosPaqueteria;

    public ClassViaje() {}

    public int getId() { return idViaje; }

    private String formatearFechaHora() {
        if (fechayHora != null && fechayHora.length() >= 16) {
            return fechayHora.substring(0, 16).replace("T", " ");
        } else {
            return fechayHora;
        }
    }

    @Override
    public String toString() {
        return idViaje + " - " + formatearFechaHora() + " - " + cuposLlenosPasajeros + " - " + cantidadCuposPasajeros + " - " +
            ciudadOrigen + " - " + ciudadDestino + " - " + cuposLlenosPaqueteria + " - " + cantidadCuposPaqueteria + " - " + "(" + idBus + ")";
    }
}
