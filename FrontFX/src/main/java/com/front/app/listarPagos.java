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
import java.util.function.Consumer;

public class listarPagos {

    private static ObservableList<String> items = FXCollections.observableArrayList();

    public static Scene crearEscena(Stage stage) {
        Label titulo = new Label("Pagos Registrados");
        titulo.setStyle("-fx-font-style: italic;");

        ListView<String> lista = new ListView<>(items);
        lista.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String pago, boolean empty) {
                super.updateItem(pago, empty);
                if (empty || pago == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label label = new Label(pago);
                    Button borrarBtn = new Button("Borrar");

                    borrarBtn.setOnAction(e -> {
                        enviarPost("http://localhost:8080/borrarPago", Map.of("pago", pago), success -> {
                            if (success) {
                                Platform.runLater(() -> cargarpagosDesdeServidor());
                            } else {
                                Platform.runLater(() -> {
                                    Alert alerta = new Alert(Alert.AlertType.ERROR, "No se pudo borrar el registro de pago. (conexión al servidor)");
                                    DialogPane dialogPane = alerta.getDialogPane();
                                    dialogPane.getStylesheets().add(listarConductores.class.getResource("/styles.css").toExternalForm());
                                    dialogPane.getStyleClass().add("error-alert");
                                    alerta.showAndWait();
                                });
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

        TitledPane pane = new TitledPane("Nombre de la Factura - ID Viaje - Metodo de Pago - Precio", lista);
        pane.setCollapsible(false);

        lista.setPrefHeight(300);
        lista.setStyle("-fx-font-size: 14px;");

        Button volverBtn = new Button("Volver");
        volverBtn.setOnAction(e -> {
            Scene escenaPaginaPrincipal = paginaPrincipal.crearEscena(stage);
            App.cambiarEscena(escenaPaginaPrincipal, "Pagina Principal");
        });

        VBox layout = new VBox(10, titulo, pane, volverBtn);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 600, 500);
        scene.getStylesheets().add(listarPagos.class.getResource("/styles.css").toExternalForm());

        cargarpagosDesdeServidor();

        return scene;
    }

    private static void cargarpagosDesdeServidor() {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/pagosRegistrados"))
                    .GET()
                    .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    List<String> pagos = mapper.readValue(
                        response.body(),
                        mapper.getTypeFactory().constructCollectionType(List.class, String.class)
                    );
                    Platform.runLater(() -> items.setAll(pagos));
                } else {
                    Platform.runLater(() -> {
                        Alert alerta = new Alert(Alert.AlertType.ERROR, "Error al consultar pagos.");
                        DialogPane dialogPane = alerta.getDialogPane();
                        dialogPane.getStylesheets().add(listarPagos.class.getResource("/styles.css").toExternalForm());
                        dialogPane.getStyleClass().add("error-alert");
                        alerta.showAndWait();
                    });
                }
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    Alert alerta = new Alert(Alert.AlertType.ERROR, "Error al consultar pagos.");
                    DialogPane dialogPane = alerta.getDialogPane();
                    dialogPane.getStylesheets().add(listarPagos.class.getResource("/styles.css").toExternalForm());
                    dialogPane.getStyleClass().add("error-alert");
                    alerta.showAndWait();
                });
                ex.printStackTrace();
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
                System.out.println("Respuesta del servidor: " + response.body());

                if (response.statusCode() == 200) {
                    callback.accept(true);
                } else {
                    callback.accept(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.accept(false);
            }
        }).start();
    }
}
