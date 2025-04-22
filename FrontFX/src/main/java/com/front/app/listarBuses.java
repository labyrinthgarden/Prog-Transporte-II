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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class listarBuses {

    private static ObservableList<Bus> items = FXCollections.observableArrayList();

    public static Scene crearEscena(Stage stage) {
        Label titulo = new Label("Buses Registrados");
        titulo.setStyle("-fx-font-style: italic;");

        ListView<Bus> lista = new ListView<>(items);
        lista.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Bus bus, boolean empty) {
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
                                    Alert alerta = new Alert(Alert.AlertType.ERROR, "No se pudo borrar el registro de bus. (conexion al servidor)");
                                    DialogPane dialogPane = alerta.getDialogPane();
                                    dialogPane.getStylesheets().add(listarBuses.class.getResource("/styles.css").toExternalForm());
                                    dialogPane.getStyleClass().add("error-alert");
                                    alerta.showAndWait();
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
                if (!conductorField.getText().isEmpty() && !empresaField.getText().isEmpty() &&
                    !placaField.getText().isEmpty() && !aforoField.getText().isEmpty() &&
                    !cantidadDePaquetesField.getText().isEmpty() && placaField.getText().length()<=7) {

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
                                Alert alerta = new Alert(Alert.AlertType.ERROR, "No se pudo agregar el registro de bus.");
                                DialogPane dialogPane = alerta.getDialogPane();
                                dialogPane.getStylesheets().add(listarBuses.class.getResource("/styles.css").toExternalForm());
                                dialogPane.getStyleClass().add("error-alert");
                                alerta.showAndWait();
                            });
                        }
                    });
                } else {
                    Alert alerta = new Alert(Alert.AlertType.WARNING, "Debe ingresar todos los datos.\n"+
                    "La placa debe tener maximo 7 digitos 'XXX-XXX'.");
                    DialogPane dialogPane = alerta.getDialogPane();
                    dialogPane.getStylesheets().add(listarBuses.class.getResource("/styles.css").toExternalForm());
                    dialogPane.getStyleClass().add("warning-alert");
                    alerta.showAndWait();
                }
            });

            VBox popupLayout = new VBox(10, conductorField, empresaField, placaField, aforoField, cantidadDePaquetesField, enviarBtn);
            popupLayout.setPadding(new Insets(20));
            popupLayout.setAlignment(Pos.CENTER);
            Scene popupScene = new Scene(popupLayout, 300, 300);
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
        scene.getStylesheets().add(listarBuses.class.getResource("/styles.css").toExternalForm());

        cargarBusesDesdeServidor();

        return scene;
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
                System.out.println(response.body()+" CODIGO: "+response.statusCode());

                if (response.statusCode() == 200) {
                    List<Bus> buses = mapper.readValue(
                        response.body(),
                        mapper.getTypeFactory().constructCollectionType(List.class, Bus.class)
                    );
                    Platform.runLater(() -> items.setAll(buses));
                } else {
                    System.err.println("Error al obtener buses. Código de estado: " + response.statusCode());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> {
                    Alert alerta = new Alert(Alert.AlertType.ERROR, "Error al consultar buses.");
                    DialogPane dialogPane = alerta.getDialogPane();
                    dialogPane.getStylesheets().add(listarBuses.class.getResource("/styles.css").toExternalForm());
                    dialogPane.getStyleClass().add("error-alert");
                    alerta.showAndWait();
                });
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
