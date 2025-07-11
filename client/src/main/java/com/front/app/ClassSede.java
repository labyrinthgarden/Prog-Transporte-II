package com.front.app;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClassSede {
    @JsonProperty("nombre")
    private String nombre;
    @JsonProperty("direccion")
    private String direccion;

    public ClassSede() {}

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre + " - " + direccion;
    }
}
