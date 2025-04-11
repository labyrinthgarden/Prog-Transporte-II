package com.front.app;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class listarPagos {
    public static Scene crearEscena(Stage stage) {
        Label titulo = new Label("Pagos Registrados");
        titulo.setStyle("-fx-font-style:italic;");

        // ObservableList<String> items = FXCollections.observableArrayList();
        // for (Object dato : datos) {
        //     items.add(dato.toString());
        // }
        // ListView<String> lista = new ListView<>(items);
        // lista.setPrefHeight(300);
        // lista.setStyle("-fx-font-size: 14px;");

        // Label costoLabel = new Label("Costo final: "+costo);
        // costoLabel.setStyle("-fx-font-style:italic; -fx-font-size:15px;");

        Label mensaje = new Label();
        mensaje.setId("feedback");

        Button volverBtn = new Button("Volver");
        volverBtn.setOnAction(e -> {
            Scene escenaPaginaPrincipal = paginaPrincipal.crearEscena(stage);
            App.cambiarEscena(escenaPaginaPrincipal, "Pagina Principal");
        });

        VBox layout = new VBox(10,
            titulo,
            // lista,
            // costoLabel,
            volverBtn,
            mensaje
        );
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 600, 500);
        scene.getStylesheets().add(listarPagos.class.getResource("/styles.css").toExternalForm());
        return scene;
    }
}
