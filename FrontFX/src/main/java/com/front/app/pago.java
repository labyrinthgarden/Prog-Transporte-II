package com.front.app;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class pago {
    private static void enviarReservaAlServidorFormato1(Object[] datos, List<String> infoPago) {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                Map<String, Object> body = new HashMap<>();
                body.put("datos", datos);
                body.put("infoPago", infoPago);

                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(body);
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

    private static void enviarReservaAlServidorFormato2(Object[] datos, List<String> infoPago) {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                Map<String, Object> body = new HashMap<>();
                body.put("datos", datos);
                body.put("infoPago", infoPago);

                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(body);
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/reservarEnvio"))
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

    public static Scene crearEscena(Stage stage, Stage popupStage, Object[] datos, int determinante) {
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
        List<String> infoPago = new ArrayList<String>();
        metodoDePago.setOnAction(e -> {
            infoPago.clear();
            if ("Efectivo al llegar".equals(metodoDePago.getValue())) {
                numeroTarjeta.setDisable(true);
                cvv.setDisable(true);
                infoPago.add(metodoDePago.getValue());
            } else {
                numeroTarjeta.setDisable(false);
                cvv.setDisable(false);
                infoPago.add(metodoDePago.getValue());
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
            } else if (!("Efectivo al llegar".equals(metodoDePago.getValue())) && (cvv.getText().length()>3 || cvv.getText().length()<3)) {
                mensaje.setText("El cvv debe tener 3 digitos.");
                return;
            }
            infoPago.add(numeroTarjeta.getText());
            infoPago.add(String.valueOf(costo[0]));
            mensaje.setText("Listo");
            if (determinante == 1) {
                enviarReservaAlServidorFormato1(datos,infoPago);
            } else if (determinante == 2) {
                enviarReservaAlServidorFormato2(datos,infoPago);
            }
            Scene escenaInformacionFactura = ticket.crearEscena(stage,datos,costo[0]);
            App.cambiarEscena(escenaInformacionFactura, "Ticket");
            popupStage.close();
        });

        Button volverBtn = new Button("Volver atras");
        volverBtn.setOnAction(e -> {
            popupStage.close();
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
