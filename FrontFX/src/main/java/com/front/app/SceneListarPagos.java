package com.front.app;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class SceneListarPagos {

    private static ObservableList<ClassPagos> items = FXCollections.observableArrayList();
    private static ObservableList<ClassPagosEnvios> items2 = FXCollections.observableArrayList();
    private static FilteredList<ClassPagos> filteredItems = new FilteredList<>(items);
    private static FilteredList<ClassPagosEnvios> filteredItems2 = new FilteredList<>(items2);

    public static Scene crearEscena(Stage stage) {
        Label titulo = new Label("Pagos de Boletos Registrados");
        titulo.setStyle("-fx-font-style: italic;");

        TextField searchField = new TextField();
        searchField.setPromptText("Escribe cualquier dato para buscar pagos");

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredItems.setPredicate(pago -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return pago.toString().toLowerCase().contains(lowerCaseFilter);
            });

            filteredItems2.setPredicate(pagoEnvio -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return pagoEnvio.toString().toLowerCase().contains(lowerCaseFilter);
            });
        });

        ListView<ClassPagos> lista1 = new ListView<>(filteredItems);
        lista1.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ClassPagos pago, boolean empty) {
                super.updateItem(pago, empty);
                if (empty || pago == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label label = new Label(pago.toString());

                    HBox hbox = new HBox(10, label);
                    label.setMaxWidth(Double.MAX_VALUE);
                    HBox.setHgrow(label, Priority.ALWAYS);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(hbox);
                }
            }
        });

        TitledPane pane1 = new TitledPane("Nombre de la Factura - Precio - Numero Tarjeta - Metodo de Pago - ID Boleto - ID Pago Boleto", lista1);
        pane1.setCollapsible(true);
        lista1.setPrefHeight(300);
        lista1.setStyle("-fx-font-size: 14px;");

        Label titulo2 = new Label("Pagos de Envios Registrados");
        titulo2.setStyle("-fx-font-style: italic;");

        ListView<ClassPagosEnvios> lista2 = new ListView<>(filteredItems2);
        lista2.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ClassPagosEnvios pago, boolean empty) {
                super.updateItem(pago, empty);
                if (empty || pago == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label label = new Label(pago.toString());

                    HBox hbox = new HBox(10, label);
                    label.setMaxWidth(Double.MAX_VALUE);
                    HBox.setHgrow(label, Priority.ALWAYS);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(hbox);
                }
            }
        });

        TitledPane pane2 = new TitledPane("Nombre de la Factura - Precio - Numero Tarjeta - Metodo de Pago - ID Envio - ID Pago Envio", lista2);
        pane2.setCollapsible(true);
        lista2.setPrefHeight(300);
        lista2.setStyle("-fx-font-size: 14px;");

        Button volverBtn = new Button("Volver");
        volverBtn.setOnAction(e -> {
            Scene escenaPaginaPrincipal = ScenePaginaPrincipal.crearEscena(stage);
            App.cambiarEscena(escenaPaginaPrincipal, "Pagina Principal");
        });

        VBox layout = new VBox(10, searchField, titulo, pane1, titulo2, pane2, volverBtn);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 600, 500);
        scene.getStylesheets().add(SceneListarPagos.class.getResource("/styles.css").toExternalForm());

        cargarPagosDesdeServidor();
        cargarPagosEnviosDesdeServidor();

        return scene;
    }

    private static void cargarPagosDesdeServidor() {
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
                    List<ClassPagos> pagos = mapper.readValue(
                        response.body(),
                        mapper.getTypeFactory().constructCollectionType(List.class, ClassPagos.class)
                    );
                    Platform.runLater(() -> items.setAll(pagos));
                } else {
                    Platform.runLater(() -> mostrarError("Error al consultar pagos. Código: " + response.statusCode()));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> mostrarError("Error al consultar pagos."));
            }
        }).start();
    }

    private static void cargarPagosEnviosDesdeServidor() {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/pagosEnviosRegistrados"))
                    .GET()
                    .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    List<ClassPagosEnvios> pagos = mapper.readValue(
                        response.body(),
                        mapper.getTypeFactory().constructCollectionType(List.class, ClassPagosEnvios.class)
                    );
                    Platform.runLater(() -> items2.setAll(pagos));
                } else {
                    Platform.runLater(() -> mostrarError("Error al consultar pagos. Código: " + response.statusCode()));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> mostrarError("Error al consultar pagos."));
            }
        }).start();
    }

    private static void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje);
        DialogPane dialogPane = alerta.getDialogPane();
        dialogPane.getStylesheets().add(SceneListarPagos.class.getResource("/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("error-alert");
        alerta.showAndWait();
    }
}
