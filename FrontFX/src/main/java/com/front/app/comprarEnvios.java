package com.front.app;
import java.util.Arrays;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class comprarEnvios {
    public static Scene crearEscena(Stage stage) {
        List<String> sedes = Arrays.asList(
            "Sede de Aguachica", "Sede de Aguazul", "Sede de Apartadó", "Sede de Arauca", "Sede de Arjona", "Sede de Armenia", "Sede de Barrancabermeja", "Sede de Barranquilla", "Sede de Bello", "Sede de Bogota",
            "Sede de Bucaramanga", "Sede de Buga", "Sede de Cajicá", "Sede de Caldas", "Sede de Cali", "Sede de Campo de la Cruz", "Sede de Cartago", "Sede de Cartagena", "Sede de Carmen de Bolívar", "Sede de Cereté",
            "Sede de Chía", "Sede de Chinchiná", "Sede de Chiquinquirá", "Sede de Ciénaga", "Sede de Cúcuta", "Sede de Cumaral", "Sede de Copacabana", "Sede de Duitama", "Sede de El Bagre", "Sede de El Banco",
            "Sede de El Espinal", "Sede de Envigado", "Sede de Facatativá", "Sede de Florencia", "Sede de Floridablanca", "Sede de Funza", "Sede de Fusagasugá", "Sede de Galapa", "Sede de Garzón", "Sede de Girón",
            "Sede de Guadalajara de Buga", "Sede de Guarne", "Sede de Honda", "Sede de Ibague", "Sede de Ipiales", "Sede de Itagüí", "Sede de Jamundí", "Sede de La Ceja", "Sede de La Dorada", "Sede de La Estrella",
            "Sede de La Jagua de Ibirico", "Sede de Leticia", "Sede de Leticia", "Sede de Lorica", "Sede de Maicao", "Sede de Magangué", "Sede de Malambo", "Sede de Manizales", "Sede de Madrid", "Sede de Malambo",
            "Sede de Medellin", "Sede de Mitú", "Sede de Mocoa", "Sede de Montería", "Sede de Montelíbano", "Sede de Mosquera", "Sede de Neiva", "Sede de Ocaña", "Sede de Ocobos", "Sede de Palmira", "Sede de Pasto",
            "Sede de Pereira", "Sede de Pereira", "Sede de Piedecuesta", "Sede de Pitalito", "Sede de Planeta Rica", "Sede de Popayán", "Sede de Puerto Asís", "Sede de Puerto Carreño", "Sede de Puerto López", "Sede de Quibdó",
            "Sede de Rionegro", "Sede de Riohacha", "Sede de Riosucio", "Sede de Sabaneta", "Sede de Sahagún", "Sede de San Andrés de Sotavento", "Sede de San Gil", "Sede de San José del Guaviare", "Sede de Santa Marta", "Sede de Soacha",
            "Sede de Soledad", "Sede de Sogamoso", "Sede de Sincelejo", "Sede de Tame", "Sede de Tierralta", "Sede de Tunja", "Sede de Turbo", "Sede de Turbaco", "Sede de Tuluá", "Sede de Valledupar", "Sede de Villamaría",
            "Sede de Villavicencio", "Sede de Yopal", "Sede de Zipaquirá"
        );

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
        sedeEnvio.getItems().addAll(sedes);
        sedeEnvio.setPromptText("Sede de envio");

        ComboBox<String> sedeEntrega = new ComboBox<>();
        sedeEntrega.getItems().addAll(sedes);
        sedeEntrega.setPromptText("Sede de entrega");

        TextField pesoPaquete = new TextField();
        pesoPaquete.setPromptText("Peso del paquete (Kg)");

        Label mensaje = new Label();
        mensaje.setId("feedback");

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

            Scene escenaPago = pago.crearEscena(stage,datos,2);
            App.cambiarEscena(escenaPago, "Checkout");
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

        Scene scene = new Scene(layout, 600, 500);
        scene.getStylesheets().add(comprarEnvios.class.getResource("/styles.css").toExternalForm());
        return scene;
    }
}
