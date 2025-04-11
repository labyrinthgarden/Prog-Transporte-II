package com.front.app;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class pago {
    private static void enviarReservaAlServidorFormato1(Object[] datos) {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(datos);
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/reservarViaje"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    System.out.println("Reserva enviada correctamente");
                } else {
                    System.err.println("Error en la reserva: " + response.body());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private static void enviarReservaAlServidorFormato2(Object[] datos) {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(datos);
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:3000/reservarEnvio"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    System.out.println("Reserva enviada correctamente");
                } else {
                    System.err.println("Error en la reserva: " + response.body());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public static Scene crearEscena(Stage stage, Object[] datos, int determinante) {
        final int[] costo = {30000};
        if (determinante == 1) {
            if (datos[5].equals("True")) {
                costo[0] += 20000 * Integer.parseInt(datos[6].toString());
            } else {
                costo[0] += 0;
            }
        } else if (determinante == 2) {
            costo[0] = 9000 * Integer.parseInt(datos[6].toString());
        }
        Label titulo1 = new Label("Checkout");
        titulo1.setId("titulo");

        Label costoLabel = new Label("Costo: $"+costo[0]);
        costoLabel.setStyle("-fx-font-style:italic;");

        TextField numeroTarjeta = new TextField();
        numeroTarjeta.setPromptText("Numero Tarjeta");
        numeroTarjeta.setDisable(true);

        TextField cvv = new TextField();
        cvv.setPromptText("CVV");
        cvv.setDisable(true);

        ComboBox<String> metodoDePago = new ComboBox<>();
        metodoDePago.getItems().addAll("Efectivo al llegar", "VISA", "Mastercard");
        metodoDePago.setPromptText("Metodo de Pago");
        metodoDePago.setOnAction(e -> {
            if ("Efectivo al llegar".equals(metodoDePago.getValue())) {
                numeroTarjeta.setDisable(true);
                cvv.setDisable(true);
            } else {
                numeroTarjeta.setDisable(false);
                cvv.setDisable(false);
            }
        });

        Label mensaje = new Label();
        mensaje.setId("feedback");

        Button checkoutBtn = new Button("Reservar");
        checkoutBtn.setOnAction(e -> {
            if (metodoDePago.getValue() == null ||
                (!("Efectivo al llegar".equals(metodoDePago.getValue())) && numeroTarjeta.getText().isEmpty() && cvv.getText().isEmpty())
                ) {
                mensaje.setText("Completa todos los campos.");
                return;
            }
            mensaje.setText("Listo");
            if (determinante == 1) {
                enviarReservaAlServidorFormato1(datos);
            } else if (determinante == 2) {
                enviarReservaAlServidorFormato2(datos);
            }
            Scene escenaInformacionFactura = ticket.crearEscena(stage,datos,costo[0]);
            App.cambiarEscena(escenaInformacionFactura, "Ticket");
        });

        Button volverBtn = new Button("Volver al inicio");
        volverBtn.setOnAction(e -> {
            Scene escenaInicio = inicio.crearEscena(stage);
            App.cambiarEscena(escenaInicio, "Inicio");
        });

        VBox layout = new VBox(10,
            titulo1,
            costoLabel,
            metodoDePago,
            numeroTarjeta,
            cvv,
            checkoutBtn,
            volverBtn,
            mensaje
        );
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 600, 500);
        scene.getStylesheets().add(pago.class.getResource("/styles.css").toExternalForm());
        return scene;
    }
}
