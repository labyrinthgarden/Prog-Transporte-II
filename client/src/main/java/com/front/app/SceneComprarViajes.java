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
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SceneComprarViajes {
    private static void cargarOpcionesCombo(String url, ComboBox<String> comboBox) {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    List<String> opciones = mapper.readValue(
                        response.body(),
                        mapper.getTypeFactory().constructCollectionType(List.class, String.class)
                    );
                    Platform.runLater(() -> comboBox.getItems().setAll(opciones));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alerta = new Alert(Alert.AlertType.ERROR, "Error al consultar buses disponibles.");
                DialogPane dialogPane = alerta.getDialogPane();
                dialogPane.getStylesheets().add(SceneListarViajes.class.getResource("/styles.css").toExternalForm());
                dialogPane.getStyleClass().add("error-alert");
                alerta.showAndWait();
            }
        }).start();
    }
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
                    .uri(URI.create(Config.URL_BACKEND+"/fechasDisponibles"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    List<String> fechas = mapper.readValue(
                        response.body(),
                        mapper.getTypeFactory().constructCollectionType(List.class, String.class)
                    );
                    Platform.runLater(() -> {
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
        List<String> ciudades = Arrays.asList();

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

        cargarOpcionesCombo(Config.URL_BACKEND+"/listarOpcionesSedes", ciudadOrigen);
        cargarOpcionesCombo(Config.URL_BACKEND+"/listarOpcionesSedes", ciudadDestino);

        ComboBox<String> fechasDisponibles = new ComboBox<>();
        fechasDisponibles.setPromptText("Tenemos estas horas disponibles para tu viaje:");
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
            } else if (ciudadOrigen.getValue().equals(ciudadDestino.getValue())) {
                mensaje.setText("La Ciudad de Origen y de Destino no pueden ser la misma.");
                return;
            } else if (fechasDisponibles.getValue().equals("No tenemos viajes disponibles entre estas ubicaciones")) {
                mensaje.setText("Parece que no hay viajes disponibles.");
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

            Stage popup = new Stage();
            Scene escenaPago = ScenePago.crearEscena(stage,popup,datos,1);
            popup.setTitle("Checkout");
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.initOwner(stage);
            popup.setScene(escenaPago);
            popup.showAndWait();
        });

        Button volverBtn = new Button("Volver al inicio");
        volverBtn.setOnAction(e -> {
            Scene escenaInicio = SceneInicio.crearEscena(stage);
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

        Scene scene = new Scene(layout, 600, 600);
        scene.getStylesheets().add(SceneComprarViajes.class.getResource("/styles.css").toExternalForm());
        return scene;
    }
}
