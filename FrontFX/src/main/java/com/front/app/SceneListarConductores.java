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

public class SceneListarConductores {

    private static ObservableList<String> items = FXCollections.observableArrayList();

    public static Scene crearEscena(Stage stage) {
        Label titulo = new Label("Conductores Registrados");
        titulo.setStyle("-fx-font-style: italic;");

        TextField searchField = new TextField();
        searchField.setPromptText("Escribe cualquier dato para buscar conductores");

        FilteredList<String> filteredItems = new FilteredList<>(items, p -> true);

        ListView<String> lista = new ListView<>(filteredItems);
        lista.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String conductor, boolean empty) {
                super.updateItem(conductor, empty);
                if (empty || conductor == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label label = new Label(conductor);

                    HBox hbox = new HBox(10, label);
                    label.setMaxWidth(Double.MAX_VALUE);
                    HBox.setHgrow(label, Priority.ALWAYS);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(hbox);
                }
            }
        });

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            filteredItems.setPredicate(conductor -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return conductor.toLowerCase().contains(lowerCaseFilter);
            });
        });

        lista.setPrefHeight(300);
        lista.setStyle("-fx-font-size: 14px;");

        TitledPane pane = new TitledPane("Nombre", lista);
        pane.setCollapsible(false);

        Button volverBtn = new Button("Volver");
        volverBtn.setOnAction(e -> {
            Scene escenaPaginaPrincipal = ScenePaginaPrincipal.crearEscena(stage);
            App.cambiarEscena(escenaPaginaPrincipal, "Pagina Principal");
        });

        VBox layout = new VBox(10, titulo, searchField, pane, volverBtn);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 600, 500);
        scene.getStylesheets().add(SceneListarConductores.class.getResource("/styles.css").toExternalForm());

        cargarConductoresDesdeServidor();

        return scene;
    }

    private static void cargarConductoresDesdeServidor() {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Config.URL_BACKEND+"/conductoresRegistrados"))
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
                    Platform.runLater(() -> {
                        Alert alerta = new Alert(Alert.AlertType.ERROR, "Error al consultar conductores.");
                        DialogPane dialogPane = alerta.getDialogPane();
                        dialogPane.getStylesheets().add(SceneListarConductores.class.getResource("/styles.css").toExternalForm());
                        dialogPane.getStyleClass().add("error-alert");
                        alerta.showAndWait();
                    });
                }
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    Alert alerta = new Alert(Alert.AlertType.ERROR, "Error al consultar conductores.");
                    DialogPane dialogPane = alerta.getDialogPane();
                    dialogPane.getStylesheets().add(SceneListarConductores.class.getResource("/styles.css").toExternalForm());
                    dialogPane.getStyleClass().add("error-alert");
                    alerta.showAndWait();
                });
                ex.printStackTrace();
            }
        }).start();
    }
}
