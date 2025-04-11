package com.front.app;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class comprarViajes {
    private static void consultarFechas(String origen, String destino, ComboBox<String> fechasCombo) {
        if (origen == null || destino == null || origen.equals(destino)) {
            return;
        }
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(new OrigenDestino(origen, destino));
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/fechas-disponibles"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    List<String> fechas = mapper.convertValue(
                        mapper.readTree(response.body()).get("fechas"),
                        mapper.getTypeFactory().constructCollectionType(List.class, String.class)
                    );
                    Platform.runLater(() -> {
                        System.out.println(fechas);
                        fechasCombo.getItems().setAll(fechas);
                        fechasCombo.setDisable(false);
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
    static class OrigenDestino {
        public String origen, destino;
        public OrigenDestino(String origen, String destino) {
            this.origen = origen;
            this.destino = destino;
        }
    }
    public static Scene crearEscena(Stage stage) {
        List<String> ciudades = Arrays.asList(
            "Aguachica", "Aguazul", "Apartadó", "Arauca", "Arjona", "Armenia", "Barrancabermeja", "Barranquilla", "Bello", "Bogota",
            "Bucaramanga", "Buga", "Cajicá", "Caldas", "Cali", "Campo de la Cruz", "Cartago", "Cartagena", "Carmen de Bolívar", "Cereté",
            "Chía", "Chinchiná", "Chiquinquirá", "Ciénaga", "Cúcuta", "Cumaral", "Copacabana", "Duitama", "El Bagre", "El Banco",
            "El Espinal", "Envigado", "Facatativá", "Florencia", "Floridablanca", "Funza", "Fusagasugá", "Galapa", "Garzón", "Girón",
            "Guadalajara de Buga", "Guarne", "Honda", "Ibague", "Ipiales", "Itagüí", "Jamundí", "La Ceja", "La Dorada", "La Estrella",
            "La Jagua de Ibirico", "Leticia", "Leticia", "Lorica", "Maicao", "Magangué", "Malambo", "Manizales", "Madrid", "Malambo",
            "Medellin", "Mitú", "Mocoa", "Montería", "Montelíbano", "Mosquera", "Neiva", "Ocaña", "Ocobos", "Palmira", "Pasto",
            "Pereira", "Pereira", "Piedecuesta", "Pitalito", "Planeta Rica", "Popayán", "Puerto Asís", "Puerto Carreño", "Puerto López", "Quibdó",
            "Rionegro", "Riohacha", "Riosucio", "Sabaneta", "Sahagún", "San Andrés de Sotavento", "San Gil", "San José del Guaviare", "Santa Marta", "Soacha",
            "Soledad", "Sogamoso", "Sincelejo", "Tame", "Tierralta", "Tunja", "Turbo", "Turbaco", "Tuluá", "Valledupar", "Villamaría",
            "Villavicencio", "Yopal", "Zipaquirá"
        );

        Label titulo1 = new Label("Cuentanos mas de tu viaje");
        titulo1.setId("titulo");

        TextField nombreUsuario = new TextField();
        nombreUsuario.setPromptText("Nombre Completo");

        TextField idUsuario = new TextField();
        idUsuario.setPromptText("Numero de Identificacion");

        ComboBox<String> ciudadOrigen = new ComboBox<>();
        ciudadOrigen.getItems().addAll(ciudades);
        ciudadOrigen.setPromptText("Ciudad de origen");


        ComboBox<String> ciudadDestino = new ComboBox<>();
        ciudadDestino.getItems().addAll(ciudades);
        ciudadDestino.setPromptText("Ciudad de Destino");

        ComboBox<String> fechasDisponibles = new ComboBox<>();
        fechasDisponibles.setPromptText("Tenemos estas fechas disponibles para tu viaje:");
        fechasDisponibles.setDisable(true);
        ciudadOrigen.setOnAction(e -> consultarFechas(ciudadOrigen.getValue(), ciudadDestino.getValue(), fechasDisponibles));
        ciudadDestino.setOnAction(e -> consultarFechas(ciudadOrigen.getValue(), ciudadDestino.getValue(), fechasDisponibles));

        TextField cantidadEquipaje = new TextField();
        cantidadEquipaje.setPromptText("Cantidad de Equipaje");
        cantidadEquipaje.setDisable(true);

        TextField pesoTotalEquipaje = new TextField();
        pesoTotalEquipaje.setPromptText("Peso en Total del Equipaje");
        pesoTotalEquipaje.setDisable(true);

        CheckBox hayEquipaje = new CheckBox("¿Equipaje?");
        hayEquipaje.setStyle("-fx-padding: 30 0 0 0;");
        hayEquipaje.setOnAction(e -> {
            boolean seleccionado = hayEquipaje.isSelected();
            cantidadEquipaje.setDisable(!seleccionado);
            pesoTotalEquipaje.setDisable(!seleccionado);
        });

        Label mensaje = new Label();
        mensaje.setId("feedback");

        Button pagarBtn = new Button("Pagar");
        pagarBtn.setOnAction(e -> {
            if (nombreUsuario.getText().isEmpty() ||
                idUsuario.getText().isEmpty() ||
                ciudadOrigen.getValue() == null ||
                ciudadDestino.getValue() == null ||
                fechasDisponibles.getValue() == null ||
                (hayEquipaje.isSelected() && cantidadEquipaje.getText().isEmpty() && pesoTotalEquipaje.getText().isEmpty())
                ) {
                mensaje.setText("Completa todos los campos.");
                return;
            } else if (ciudadOrigen.getValue() == ciudadDestino.getValue()) {
                mensaje.setText("La Ciudad de Origen y de Destino no pueden ser la misma.");
                return;
            }
            Object[] datos = {
                nombreUsuario.getText(),
                idUsuario.getText(),
                ciudadOrigen.getValue(),
                ciudadDestino.getValue(),
                fechasDisponibles.getValue(),
                hayEquipaje.isSelected(),
                cantidadEquipaje.getText(),
                pesoTotalEquipaje.getText()
            };

            Scene escenaPago = pago.crearEscena(stage,datos,1);
            App.cambiarEscena(escenaPago, "Checkout");
        });

        Button volverBtn = new Button("Volver al inicio");
        volverBtn.setOnAction(e -> {
            Scene escenaInicio = inicio.crearEscena(stage);
            App.cambiarEscena(escenaInicio, "Inicio");
        });

        VBox layout = new VBox(10,
            titulo1,
            nombreUsuario,
            idUsuario,
            ciudadOrigen,
            ciudadDestino,
            fechasDisponibles,
            hayEquipaje,
            cantidadEquipaje,
            pesoTotalEquipaje,
            pagarBtn,
            volverBtn,
            mensaje
        );
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 600, 500);
        scene.getStylesheets().add(comprarViajes.class.getResource("/styles.css").toExternalForm());
        return scene;
    }
}
