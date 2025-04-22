package com.front.app;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pagos {
    @JsonProperty("idPago")
    private int idPago;
    @JsonProperty("idBoleto")
    private String idBoleto;
    @JsonProperty("metodoPago")
    private String metodoPago;
    @JsonProperty("precio")
    private String precio;

    public Pagos() {}

    public int getId() { return idPago; }

    @Override
    public String toString() {
        return idPago + " - " + idBoleto + " - " + metodoPago + " - " + precio;
    }
}
