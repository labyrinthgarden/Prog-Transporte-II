package com.front.app;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class listarPaqueteria {

    private static ObservableList<Paquete> items = FXCollections.observableArrayList();

    public static Scene crearEscena(Stage stage) {
        Label titulo = new Label("Paquetería Registrada");
        titulo.setStyle("-fx-font-style: italic;");

        ListView<Paquete> lista = new ListView<>(items);
        lista.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Paquete paquete, boolean empty) {
                super.updateItem(paquete, empty);
                if (empty || paquete == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label label = new Label(paquete.toString());
                    Button borrarBtn = new Button("Borrar");

                    borrarBtn.setOnAction(e -> {
                        enviarPost("http://localhost:8080/borrarPaquete",
                            Map.of("idPaquete", String.valueOf(paquete.getId())),
                            success -> {
                                if (success) {
                                    Platform.runLater(() -> cargarPaqueteriaDesdeServidor());
                                } else {
                                    Platform.runLater(() -> mostrarError("No se pudo borrar el paquete."));
                                }
                            });
                    });

                    HBox hbox = new HBox(10, label, borrarBtn);
                    label.setMaxWidth(Double.MAX_VALUE);
                    HBox.setHgrow(label, Priority.ALWAYS);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(hbox);
                }
            }
        });

        lista.setPrefHeight(300);
        lista.setStyle("-fx-font-size: 14px;");

        TitledPane pane = new TitledPane("ID Viaje - Nombre Remitente - ID Remitente - Nombre Destinatario - ID Destinatario - Peso", lista);
        pane.setCollapsible(false);

        Button volverBtn = new Button("Volver");
        volverBtn.setOnAction(e -> {
            Scene escenaPaginaPrincipal = paginaPrincipal.crearEscena(stage);
            App.cambiarEscena(escenaPaginaPrincipal, "Pagina Principal");
        });

        VBox layout = new VBox(10, titulo, pane, volverBtn);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 800, 500);
        scene.getStylesheets().add(listarPaqueteria.class.getResource("/styles.css").toExternalForm());

        cargarPaqueteriaDesdeServidor();

        return scene;
    }

    private static void cargarPaqueteriaDesdeServidor() {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/paqueteriaRegistrada"))
                    .GET()
                    .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.body());

                if (response.statusCode() == 200) {
                    List<Paquete> paqueteria = mapper.readValue(
                        response.body(),
                        mapper.getTypeFactory().constructCollectionType(List.class, Paquete.class)
                    );
                    Platform.runLater(() -> items.setAll(paqueteria));
                } else {
                    Platform.runLater(() -> mostrarError("Error al consultar paquetería. Código: " + response.statusCode()));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> mostrarError("Error al consultar paquetería."));
            }
        }).start();
    }

    private static void enviarPost(String url, Map<String, String> datos, Consumer<Boolean> callback) {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();

                String json = mapper.writeValueAsString(datos);

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                callback.accept(response.statusCode() == 200);
            } catch (Exception e) {
                e.printStackTrace();
                callback.accept(false);
            }
        }).start();
    }

    private static void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje);
        DialogPane dialogPane = alerta.getDialogPane();
        dialogPane.getStylesheets().add(listarPaqueteria.class.getResource("/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("error-alert");
        alerta.showAndWait();
    }
}
