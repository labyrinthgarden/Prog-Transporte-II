package com.front.app;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClassPagosEnvios {
    @JsonProperty("nombreRemitente")
    private String nombreRemitente;
    @JsonProperty("idPago")
    private int idPago;
    @JsonProperty("idFacturaEnvio")
    private String idBoleto;
    @JsonProperty("metodoPago")
    private String metodoPago;
    @JsonProperty("numeroTarjeta")
    private long numeroTarjeta;
    @JsonProperty("precio")
    private String precio;

    public ClassPagosEnvios() {}

    public int getId() { return idPago; }

    @Override
    public String toString() {
        return nombreRemitente + " - " + precio + " - " + numeroTarjeta + " - " + metodoPago + " - " + idBoleto + " - " + idPago;
    }
}
