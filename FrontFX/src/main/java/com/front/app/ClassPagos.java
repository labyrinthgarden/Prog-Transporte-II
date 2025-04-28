package com.front.app;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClassPagos {
    @JsonProperty("nombreUsuario")
    private String nombreUsuario;
    @JsonProperty("idPago")
    private int idPago;
    @JsonProperty("idBoleto")
    private String idBoleto;
    @JsonProperty("metodoPago")
    private String metodoPago;
    @JsonProperty("numeroTarjeta")
    private int numeroTarjeta;
    @JsonProperty("precio")
    private String precio;

    public ClassPagos() {}

    public int getId() { return idPago; }

    @Override
    public String toString() {
        return nombreUsuario + " - " + precio + " - " + numeroTarjeta + " - " + metodoPago + " - " + idBoleto + " - " + idPago;
    }
}
