package com.front.app;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

public class comprarEnvios {
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
                dialogPane.getStylesheets().add(listarViajes.class.getResource("/styles.css").toExternalForm());
                dialogPane.getStyleClass().add("error-alert");
                alerta.showAndWait();
            }
        }).start();
    }
    public static Scene crearEscena(Stage stage) {

        Label titulo1 = new Label("Remitente:");
        titulo1.setId("titulo");

        TextField nombreRemitente = new TextField();
        nombreRemitente.setPromptText("Nombre Completo");

        TextField idRemitente = new TextField();
        idRemitente.setPromptText("Numero de Identificacion");

        Label titulo2 = new Label("Destinatario:");
        titulo2.setId("titulo");

        TextField nombreDestinatario = new TextField();
        nombreDestinatario.setPromptText("Nombre Completo");

        TextField idDestinatario = new TextField();
        idDestinatario.setPromptText("Numero de Identificacion");

        ComboBox<String> sedeEnvio = new ComboBox<>();
        sedeEnvio.setPromptText("Sede de envio");

        ComboBox<String> sedeEntrega = new ComboBox<>();
        sedeEntrega.setPromptText("Sede de entrega");

        TextField pesoPaquete = new TextField();
        pesoPaquete.setPromptText("Peso del paquete (Kg)");

        Label mensaje = new Label();
        mensaje.setId("feedback");

        cargarOpcionesCombo("http://localhost:8080/listarOpcionesSedes", sedeEnvio);
        cargarOpcionesCombo("http://localhost:8080/listarOpcionesSedes", sedeEntrega);

        Button pagarBtn = new Button("Pagar");
        pagarBtn.setOnAction(e -> {
            if (nombreRemitente.getText().isEmpty() ||
                idRemitente.getText().isEmpty() ||
                nombreDestinatario.getText().isEmpty() ||
                idDestinatario.getText().isEmpty() ||
                sedeEnvio.getValue() == null ||
                sedeEntrega.getValue() == null ||
                pesoPaquete.getText().isEmpty()) {
                mensaje.setText("Completa todos los campos.");
                return;
            } else if (sedeEnvio.getValue() == sedeEntrega.getValue()) {
                mensaje.setText("La Sede de Origen y de Destino no pueden ser la misma.");
                return;
            }
            Object[] datos = {
                nombreRemitente.getText(),
                idRemitente.getText(),
                nombreDestinatario.getText(),
                idDestinatario.getText(),
                sedeEnvio.getValue(),
                sedeEntrega.getValue(),
                pesoPaquete.getText()
            };

            Stage popup = new Stage();
            Scene escenaPago = pago.crearEscena(stage,popup,datos,2);
            popup.setTitle("Checkout");
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.initOwner(stage);
            popup.setScene(escenaPago);
            popup.showAndWait();
        });

        Button volverBtn = new Button("Volver al inicio");
        volverBtn.setOnAction(e -> {
            Scene escenaInicio = inicio.crearEscena(stage);
            App.cambiarEscena(escenaInicio, "Inicio");
        });

        VBox layout = new VBox(10,
            titulo1,
            nombreRemitente,
            idRemitente,
            titulo2,
            nombreDestinatario,
            idDestinatario,
            sedeEnvio,
            sedeEntrega,
            pesoPaquete,
            pagarBtn,
            volverBtn,
            mensaje
        );
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 600, 600);
        scene.getStylesheets().add(comprarEnvios.class.getResource("/styles.css").toExternalForm());
        return scene;
    }
}
