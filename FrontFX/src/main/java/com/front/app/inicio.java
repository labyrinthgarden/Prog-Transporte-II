package com.front.app;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class inicio {
    public static Scene crearEscena(Stage stage) {
        Button comprarViajesBtn = new Button("Comprar Viajes");
        comprarViajesBtn.setId("boton-inicio");
        comprarViajesBtn.setOnMouseClicked((MouseEvent e) -> {
            Scene escenaComprarViajes = comprarViajes.crearEscena(stage);
            stage.setScene(escenaComprarViajes);
            stage.setTitle("Viajes");
        });

        Button comprarEnviosBtn = new Button("Comprar Envios");
        comprarEnviosBtn.setId("boton-inicio");
        comprarEnviosBtn.setOnMouseClicked((MouseEvent e) -> {
            Scene escenaComprarEnvios = comprarEnvios.crearEscena(stage);
            stage.setScene(escenaComprarEnvios);
            stage.setTitle("Envios");
        });

        Label login = new Label("¿Administrador?, Logueate");
        login.setStyle("-fx-text-fill: blue; -fx-underline: true; -fx-font-size: 12px;");
        login.setOnMouseClicked((MouseEvent e) -> {
            Scene escenaIniciarSesion = iniciarSesion.crearEscena(stage);
            stage.setScene(escenaIniciarSesion);
            stage.setTitle("Login");
        });

        HBox botonesCentro = new HBox(20, comprarViajesBtn, comprarEnviosBtn);
        botonesCentro.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setCenter(botonesCentro);

        HBox topRight = new HBox(login);
        topRight.setPadding(new Insets(10));
        topRight.setAlignment(Pos.TOP_RIGHT);

        root.setTop(topRight);

        Scene scene = new Scene(root, 600, 500);
        scene.getStylesheets().add(inicio.class.getResource("/styles.css").toExternalForm());
        return scene;
    }
}
