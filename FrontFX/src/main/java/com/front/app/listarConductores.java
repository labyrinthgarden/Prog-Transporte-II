package com.front.app;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class listarConductores {

    private static ObservableList<String> items = FXCollections.observableArrayList();

    public static Scene crearEscena(Stage stage) {
        Label titulo = new Label("Conductores Registrados");
        titulo.setStyle("-fx-font-style: italic;");

        ListView<String> lista = new ListView<>(items);
        lista.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String conductor, boolean empty) {
                super.updateItem(conductor, empty);
                if (empty || conductor == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label label = new Label(conductor);
                    Button borrarBtn = new Button("Borrar");

                    borrarBtn.setOnAction(e -> enviarPost("http://localhost:8080/borrar-conductor", conductor));

                    HBox hbox = new HBox(10, label, borrarBtn);
                    label.setMaxWidth(Double.MAX_VALUE);
                    HBox.setHgrow(label, Priority.ALWAYS);
                    HBox.setHgrow(label, Priority.ALWAYS);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(hbox);
                }
            }
        });

        lista.setPrefHeight(300);
        lista.setStyle("-fx-font-size: 14px;");

        Button volverBtn = new Button("Volver");
        volverBtn.setOnAction(e -> {
            Scene escenaPaginaPrincipal = paginaPrincipal.crearEscena(stage);
            App.cambiarEscena(escenaPaginaPrincipal, "Pagina Principal");
        });

        VBox layout = new VBox(10, titulo, lista, volverBtn);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 600, 500);
        scene.getStylesheets().add(listarConductores.class.getResource("/styles.css").toExternalForm());

        cargarconductoresDesdeServidor();

        return scene;
    }

    private static void cargarconductoresDesdeServidor() {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/conductores-registrados"))
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    List<String> conductores = mapper.readValue(
                            response.body(),
                            mapper.getTypeFactory().constructCollectionType(List.class, String.class)
                    );
                    Platform.runLater(() -> items.setAll(conductores));
                } else {
                    System.err.println("Error al obtener conductores. Código de estado: " + response.statusCode());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private static void enviarPost(String url, String conductor) {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                String json = new ObjectMapper().writeValueAsString(Map.of("conductor", conductor));
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Respuesta del servidor: " + response.body());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
