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
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class SceneListarBuses {

    private static ObservableList<ClassBus> items = FXCollections.observableArrayList();

    public static Scene crearEscena(Stage stage) {
        Label titulo = new Label("Buses Registrados");
        titulo.setStyle("-fx-font-style: italic;");

        TextField searchField = new TextField();
        searchField.setPromptText("Escribe cualquier dato para buscar buses");

        FilteredList<ClassBus> filteredItems = new FilteredList<>(items, p -> true);

        ListView<ClassBus> lista = new ListView<>(filteredItems);
        lista.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ClassBus bus, boolean empty) {
                super.updateItem(bus, empty);
                if (empty || bus == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label label = new Label(bus.toString());
                    Button borrarBtn = new Button("Borrar");

                    borrarBtn.setOnAction(e -> {
                        enviarPost("http://localhost:8080/borrarBus", Map.of("bus", bus.getId()), success -> {
                            if (success) {
                                Platform.runLater(() -> cargarBusesDesdeServidor());
                            } else {
                                Platform.runLater(() -> {
                                    mostrarAlerta(Alert.AlertType.ERROR, "No se pudo borrar el registro de bus.");
                                });
                            }
                        });
                    });

                    HBox hbox = new HBox(10, label, borrarBtn);
                    hbox.setPadding(new Insets(5));
                    label.setMaxWidth(Double.MAX_VALUE);
                    HBox.setHgrow(label, Priority.ALWAYS);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(hbox);
                }
            }
        });

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            filteredItems.setPredicate(bus -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return bus.toString().toLowerCase().contains(lowerCaseFilter);
            });
        });

        lista.setPrefHeight(300);
        lista.setStyle("-fx-font-size: 14px;");

        TitledPane pane = new TitledPane("Conductor - Empresa - Placa - Aforo - Capacidad de Carga", lista);
        pane.setCollapsible(false);

        Button agregarBtn = new Button("+ Agregar");
        agregarBtn.setOnAction(e -> {
            Stage popup = new Stage();
            popup.setTitle("Agregar Nuevo Bus");

            popup.initModality(Modality.APPLICATION_MODAL);
            popup.initOwner(stage);

            TextField conductorField = new TextField();
            TextField empresaField = new TextField();
            TextField placaField = new TextField();
            TextField aforoField = new TextField();
            TextField cantidadDePaquetesField = new TextField();
            conductorField.setPromptText("Conductor");
            empresaField.setPromptText("Empresa");
            placaField.setPromptText("Placa");
            aforoField.setPromptText("Aforo");
            cantidadDePaquetesField.setPromptText("Capacidad de carga para paquetes");

            Button enviarBtn = new Button("Agregar");
            enviarBtn.setOnAction(ev -> {
                if (validarCampos(conductorField, empresaField, placaField, aforoField, cantidadDePaquetesField)) {
                    enviarPost("http://localhost:8080/agregarBus", Map.of(
                        "conductor", conductorField.getText(),
                        "empresa", empresaField.getText(),
                        "placa", placaField.getText(),
                        "aforo", aforoField.getText(),
                        "cantidadDePaquetes", cantidadDePaquetesField.getText()
                    ), success -> {
                        if (success) {
                            Platform.runLater(() -> {
                                popup.close();
                                cargarBusesDesdeServidor();
                            });
                        } else {
                            Platform.runLater(() -> {
                                mostrarAlerta(Alert.AlertType.ERROR, "No se pudo agregar el registro de bus.");
                            });
                        }
                    });
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING, "Debe ingresar todos los datos.\nLa placa debe tener maximo 7 digitos 'XXX-XXX'.");
                }
            });

            VBox popupLayout = new VBox(10, conductorField, empresaField, placaField, aforoField, cantidadDePaquetesField, enviarBtn);
            popupLayout.setPadding(new Insets(20));
            popupLayout.setAlignment(Pos.CENTER);
            Scene popupScene = new Scene(popupLayout, 300, 300);
            popupScene.getStylesheets().add(SceneListarSedes.class.getResource("/styles.css").toExternalForm());
            popup.setScene(popupScene);
            popup.show();
        });

        Button volverBtn = new Button("Volver");
        volverBtn.setOnAction(e -> {
            Scene escenaPaginaPrincipal = ScenePaginaPrincipal.crearEscena(stage);
            App.cambiarEscena(escenaPaginaPrincipal, "Pagina Principal");
        });

        VBox layout = new VBox(10, searchField, titulo, pane, agregarBtn, volverBtn);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 600, 500);
        scene.getStylesheets().add(SceneListarBuses.class.getResource("/styles.css").toExternalForm());

        cargarBusesDesdeServidor();

        return scene;
    }

    private static boolean validarCampos(TextField conductorField, TextField empresaField, TextField placaField, TextField aforoField, TextField cantidadDePaquetesField) {
        return !conductorField.getText().isEmpty() && !empresaField.getText().isEmpty() &&
                !placaField.getText().isEmpty() && !aforoField.getText().isEmpty() &&
                !cantidadDePaquetesField.getText().isEmpty() && placaField.getText().length() <= 7;
    }

    private static void mostrarAlerta(Alert.AlertType type, String message) {
        Alert alerta = new Alert(type, message);
        DialogPane dialogPane = alerta.getDialogPane();
        dialogPane.getStylesheets().add(SceneListarBuses.class.getResource("/styles.css").toExternalForm());
        dialogPane.getStyleClass().add(type == Alert.AlertType.ERROR ? "error-alert" : "warning-alert");
        alerta.showAndWait();
    }

    private static void cargarBusesDesdeServidor() {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/busesRegistrados"))
                    .GET()
                    .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    List<ClassBus> buses = mapper.readValue(
                        response.body(),
                        mapper.getTypeFactory().constructCollectionType(List.class, ClassBus.class)
                    );
                    Platform.runLater(() -> items.setAll(buses));
                } else {
                    System.err.println("Error al obtener buses. Código de estado: " + response.statusCode());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> mostrarAlerta(Alert.AlertType.ERROR, "Error al consultar buses."));
            }
        }).start();
    }

    private static void enviarPost(String url, Map<String, String> parametros, Consumer<Boolean> onResponse) {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                String json = new ObjectMapper().writeValueAsString(parametros);
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                boolean success = (response.statusCode() == 200);
                onResponse.accept(success);
            } catch (Exception e) {
                e.printStackTrace();
                onResponse.accept(false);
            }
        }).start();
    }
}
