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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
//import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.ListCell;
//import com.front.app.Sede;

public class listarSedes {

    private static ObservableList<Sede> items = FXCollections.observableArrayList();

    public static Scene crearEscena(Stage stage) {
        Label titulo = new Label("Sedes Registradas");
        titulo.setStyle("-fx-font-style: italic;");

        ListView<Sede> lista = new ListView<>(items);
        lista.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Sede sede, boolean empty) {
                super.updateItem(sede, empty);
                if (empty || sede == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label label = new Label(sede.toString());
                    Button borrarBtn = new Button("Borrar");

                    borrarBtn.setOnAction(e -> {
                        enviarPost("http://localhost:8080/borrarSede",
                            Map.of("sede", String.valueOf(sede.getNombre())),
                            success -> {
                                if (success) {
                                    Platform.runLater(() -> cargarSedesDesdeServidor());
                                } else {
                                    Platform.runLater(() -> {
                                        Alert alerta = new Alert(Alert.AlertType.ERROR, "No se pudo borrar el registro de sede.");
                                        DialogPane dialogPane = alerta.getDialogPane();
                                        dialogPane.getStylesheets().add(listarSedes.class.getResource("/styles.css").toExternalForm());
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

        lista.setPrefHeight(300);
        lista.setStyle("-fx-font-size: 14px;");

        TitledPane pane = new TitledPane("Ciudad - Direccion", lista);
        pane.setCollapsible(false);

        Button agregarBtn = new Button("+ Agregar");
        agregarBtn.setOnAction(e -> {
            Stage popup = new Stage();
            popup.setTitle("Agregar Nueva Sede");

            popup.initModality(Modality.APPLICATION_MODAL);
            popup.initOwner(stage);

            TextField nombreField = new TextField();
            TextField direccionField = new TextField();
            nombreField.setPromptText("Ciudad");
            direccionField.setPromptText("Direccion");

            Button enviarBtn = new Button("Agregar");
            enviarBtn.setOnAction(ev -> {
                if (!nombreField.getText().isEmpty()) {
                    enviarPost("http://localhost:8080/agregarSede", Map.of("nombreSede",nombreField.getText(),
                        "direccion",direccionField.getText()), success -> {
                        if (success) {
                            Platform.runLater(() -> cargarSedesDesdeServidor());
                        } else {
                            Platform.runLater(() -> {
                                Alert alerta = new Alert(Alert.AlertType.ERROR, "No se pudo agregar el registro de sede (conexion al servidor).");
                                DialogPane dialogPane = alerta.getDialogPane();
                                dialogPane.getStylesheets().add(listarSedes.class.getResource("/styles.css").toExternalForm());
                                dialogPane.getStyleClass().add("error-alert");
                                alerta.showAndWait();
                            });
                        }
                    });
                    popup.close();
                    cargarSedesDesdeServidor();
                } else {
                    Alert alerta = new Alert(Alert.AlertType.WARNING, "Debe ingresar un nombre.");
                    DialogPane dialogPane = alerta.getDialogPane();
                    dialogPane.getStylesheets().add(listarSedes.class.getResource("/styles.css").toExternalForm());
                    dialogPane.getStyleClass().add("warning-alert");
                    alerta.showAndWait();
                }
            });

            VBox popupLayout = new VBox(10, nombreField, direccionField, enviarBtn);
            popupLayout.setPadding(new Insets(20));
            popupLayout.setAlignment(Pos.CENTER);

            Scene popupScene = new Scene(popupLayout, 300, 200);
            popupScene.getStylesheets().add(listarSedes.class.getResource("/styles.css").toExternalForm());
            popup.setScene(popupScene);
            popup.show();
        });

        Button volverBtn = new Button("Volver");
        volverBtn.setOnAction(e -> {
            Scene escenaPaginaPrincipal = paginaPrincipal.crearEscena(stage);
            App.cambiarEscena(escenaPaginaPrincipal, "Pagina Principal");
        });

        VBox layout = new VBox(10, titulo, pane, agregarBtn, volverBtn);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 600, 500);
        scene.getStylesheets().add(listarSedes.class.getResource("/styles.css").toExternalForm());

        cargarSedesDesdeServidor();

        return scene;
    }

    private static void cargarSedesDesdeServidor() {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/sedesRegistradas"))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    List<Sede> sedes = mapper.readValue(
                        response.body(),
                        mapper.getTypeFactory().constructCollectionType(List.class, Sede.class)
                    );
                    Platform.runLater(() -> items.setAll(sedes));
                } else {
                    System.err.println("Error al obtener Sedes. Código de estado: " + response.statusCode());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> {
                    Alert alerta = new Alert(Alert.AlertType.ERROR, "Error al consultar sedes.");
                    DialogPane dialogPane = alerta.getDialogPane();
                    dialogPane.getStylesheets().add(listarSedes.class.getResource("/styles.css").toExternalForm());
                    dialogPane.getStyleClass().add("error-alert");
                    alerta.showAndWait();
                });
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
