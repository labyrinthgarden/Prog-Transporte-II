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
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import java.util.function.Consumer;

public class SceneListarViajes {
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

    private static ObservableList<ClassViaje> items = FXCollections.observableArrayList();

    public static Scene crearEscena(Stage stage) {
        Label titulo = new Label("Viajes Registrados");
        titulo.setStyle("-fx-font-style: italic;");

        TextField searchField = new TextField();
        searchField.setPromptText("Escribe cualquier dato para buscar viajes");

        FilteredList<ClassViaje> filteredItems = new FilteredList<>(items, p -> true);

        ListView<ClassViaje> lista = new ListView<>(filteredItems);
        lista.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ClassViaje viaje, boolean empty) {
                super.updateItem(viaje, empty);
                if (empty || viaje == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label label = new Label(viaje.toString());
                    label.setStyle("-fx-font-size: 17px;");
                    Button borrarBtn = new Button("Borrar");

                    borrarBtn.setOnAction(e -> {
                        enviarPost("http://localhost:8080/borrarViaje", Map.of("viaje",
                           String.valueOf(viaje.getId())), success -> {
                            if (success) {
                                Platform.runLater(() -> cargarViajesDesdeServidor());
                            } else {
                                Platform.runLater(() -> {
                                    Alert alerta = new Alert(Alert.AlertType.ERROR, "No se pudo borrar el registro de viaje. (conexion al servidor)");
                                    DialogPane dialogPane = alerta.getDialogPane();
                                    dialogPane.getStylesheets().add(SceneListarViajes.class.getResource("/styles.css").toExternalForm());
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

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            filteredItems.setPredicate(viaje -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return viaje.toString().toLowerCase().contains(lowerCaseFilter);
            });
        });


        lista.setPrefHeight(300);
        lista.setStyle("-fx-font-size: 14px;");

        TitledPane pane = new TitledPane("ID - Fecha y Hora - Pasajeros - Máximo Pasajeros - Origen - Destino - Paquetes - Máximo Paquetes - (ID Bus)", lista);
        pane.setCollapsible(false);

        Button agregarBtn = new Button("+ Agregar");
        agregarBtn.setOnAction(e -> {
            Stage popup = new Stage();
            popup.setTitle("Agregar Nuevo Viaje");

            popup.initModality(Modality.APPLICATION_MODAL);
            popup.initOwner(stage);

            ComboBox<String> busField = new ComboBox<>();
            DatePicker fechaPicker = new DatePicker();
            ComboBox<String> horaCombo = new ComboBox<>();
            ComboBox<String> minutoCombo = new ComboBox<>();
            ComboBox<String> origenField = new ComboBox<>();
            ComboBox<String> destinoField = new ComboBox<>();

            cargarOpcionesCombo("http://localhost:8080/listarOpcionesBuses", busField);
            cargarOpcionesCombo("http://localhost:8080/listarOpcionesSedes", origenField);
            cargarOpcionesCombo("http://localhost:8080/listarOpcionesSedes", destinoField);

            busField.setPromptText("Bus");
            fechaPicker.setPromptText("Fecha de Salida");

            horaCombo.setPromptText("Hora");
            minutoCombo.setPromptText("Minuto");

            for (int i = 0; i < 24; i++) {
                horaCombo.getItems().add(String.format("%02d", i));
            }
            for (int i = 0; i < 60; i += 30) {
                minutoCombo.getItems().add(String.format("%02d", i));
            }

            origenField.setPromptText("Origen");
            destinoField.setPromptText("Destino");

            Button enviarBtn = new Button("Agregar");
            enviarBtn.setOnAction(ev -> {
                if (busField.getValue() != null && fechaPicker.getValue() != null &&
                    horaCombo.getValue() != null && minutoCombo.getValue() != null &&
                    origenField.getValue() != null && destinoField.getValue() != null &&
                    !origenField.getValue().equals(destinoField.getValue())) {

                    String fechaYhora = fechaPicker.getValue().toString() + " " + horaCombo.getValue() + ":" + minutoCombo.getValue();

                    enviarPost("http://localhost:8080/agregarViaje", Map.of(
                            "bus", busField.getValue(),
                            "fechaYhora", fechaYhora,
                            "origen", origenField.getValue(),
                            "destino", destinoField.getValue()),
                            success -> {
                                if (success) {
                                    Platform.runLater(() -> cargarViajesDesdeServidor());
                                    popup.close();
                                } else {
                                    Platform.runLater(() -> {
                                        Alert alerta = new Alert(Alert.AlertType.ERROR, "No se pudo agregar el registro de viaje (conexion al servidor).");
                                        DialogPane dialogPane = alerta.getDialogPane();
                                        dialogPane.getStylesheets().add(SceneListarViajes.class.getResource("/styles.css").toExternalForm());
                                        dialogPane.getStyleClass().add("error-alert");
                                        alerta.showAndWait();
                                    });
                                }
                            }
                    );
                } else {
                    Alert alerta = new Alert(Alert.AlertType.WARNING, "Debe ingresar todos los datos. \n"+"(El destino no puede ser igual al origen)\n");
                    DialogPane dialogPane = alerta.getDialogPane();
                    dialogPane.getStylesheets().add(SceneListarViajes.class.getResource("/styles.css").toExternalForm());
                    dialogPane.getStyleClass().add("warning-alert");
                    alerta.showAndWait();
                }
            });

            VBox popupLayout = new VBox(10,
                    busField,
                    fechaPicker,
                    new HBox(5, horaCombo, minutoCombo),
                    origenField,
                    destinoField,
                    enviarBtn
            );
            popupLayout.setPadding(new Insets(20));
            popupLayout.setAlignment(Pos.CENTER);

            Scene popupScene = new Scene(popupLayout, 350, 400);
            popupScene.getStylesheets().add(SceneListarViajes.class.getResource("/styles.css").toExternalForm());
            popup.setScene(popupScene);
            popup.show();
        });

        Button volverBtn = new Button("Volver");
        volverBtn.setOnAction(e -> {
            Scene escenaPaginaPrincipal = ScenePaginaPrincipal.crearEscena(stage);
            App.cambiarEscena(escenaPaginaPrincipal, "Página Principal");
        });

        VBox layout = new VBox(10, titulo, searchField, pane, agregarBtn, volverBtn);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 750, 500);
        scene.getStylesheets().add(SceneListarViajes.class.getResource("/styles.css").toExternalForm());

        cargarViajesDesdeServidor();

        return scene;
    }

    private static void cargarViajesDesdeServidor() {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/viajesRegistrados"))
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    List<ClassViaje> viajes = mapper.readValue(
                        response.body(),
                        mapper.getTypeFactory().constructCollectionType(List.class, ClassViaje.class)
                    );
                    Platform.runLater(() -> items.setAll(viajes));
                } else {
                    System.err.println("Error al obtener viajes. Código de estado: " + response.statusCode());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> {
                    Alert alerta = new Alert(Alert.AlertType.ERROR, "Error al consultar viajes.");
                    DialogPane dialogPane = alerta.getDialogPane();
                    dialogPane.getStylesheets().add(SceneListarViajes.class.getResource("/styles.css").toExternalForm());
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
                    Platform.runLater(() -> callback.accept(true));
                } else {
                    Platform.runLater(() -> callback.accept(false));
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.accept(false);
            }
        }).start();
    }
}
