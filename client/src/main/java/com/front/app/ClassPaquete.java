package com.front.app;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClassPaquete {
    @JsonProperty("id")
    private int id;
    @JsonProperty("idViaje")
    private int idViaje;
    @JsonProperty("nombreRemitente")
    private String nombreRemitente;
    @JsonProperty("idRemitente")
    private long idRemitente;
    @JsonProperty("nombreDestinatario")
    private String nombreDestinatario;
    @JsonProperty("idDestinatario")
    private long idDestinatario;
    @JsonProperty("pesoPaquete")
    private double pesoPaquete;

    public int getId() { return id; }

    @Override
    public String toString() {
        return idViaje + " - " + nombreRemitente + " - " + idRemitente + " - " +
            nombreDestinatario + " - " + idDestinatario + " - " + pesoPaquete + " kg";
    }
}
