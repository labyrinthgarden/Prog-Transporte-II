package com.front.app;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClassBus {
    @JsonProperty("conductor")
    private String conductor;
    @JsonProperty("empresa")
    private String empresa;
    @JsonProperty("placa")
    private String placa;
    @JsonProperty("aforo")
    private int aforo;
    @JsonProperty("cantidadDePaquetes")
    private int cantidadDePaquetes;

    public ClassBus() {}

    public String getId() { return placa; }

    @Override
    public String toString() {
        return conductor + " - " + empresa + " - " + "("+placa+")" + " - " +
        aforo + " - " + cantidadDePaquetes;
    }
}
