package com.front.app;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class paginaPrincipal {
    public static Scene crearEscena(Stage stage) {
        Label titulo = new Label("Pagina Principal");
        titulo.setId("titulo");

        Button busesBtn = new Button("Listar buses");
        busesBtn.setId("boton-grande");
        busesBtn.setOnAction(e -> {
            Scene escenaListarBuses = listarBuses.crearEscena(stage);
            App.cambiarEscena(escenaListarBuses, "Buses");
        });

        Button paqueteriaBtn = new Button("Listar paqueteria");
        paqueteriaBtn.setId("boton-grande");
        paqueteriaBtn.setOnAction(e -> {
            Scene escenaListarPaqueteria = listarPaqueteria.crearEscena(stage);
            App.cambiarEscena(escenaListarPaqueteria, "Paqueteria");
        });

        Button sedesBtn = new Button("Listar sedes");
        sedesBtn.setId("boton-grande");
        sedesBtn.setOnAction(e -> {
            Scene escenaListarSedes = listarSedes.crearEscena(stage);
            App.cambiarEscena(escenaListarSedes, "Nuestras Sedes");
        });

        Button viajesBtn = new Button("Listar viajes");
        viajesBtn.setId("boton-grande");
        viajesBtn.setOnAction(e -> {
            Scene escenaListarViajes = listarViajes.crearEscena(stage);
            App.cambiarEscena(escenaListarViajes, "Viajes");
        });

        Button conductoresBtn = new Button("Listar Conductores");
        conductoresBtn.setId("boton-grande");
        conductoresBtn.setOnAction(e -> {
            Scene escenaListarConductores = listarConductores.crearEscena(stage);
            App.cambiarEscena(escenaListarConductores, "Nuestros Conductores");
        });

        Button pagosBtn = new Button("Listar pagos");
        pagosBtn.setId("boton-grande");
        pagosBtn.setOnAction(e -> {
            Scene escenaListarPagos = listarPagos.crearEscena(stage);
            App.cambiarEscena(escenaListarPagos, "Pagos Registrados");
        });

        Button volverBtn = new Button("Volver al inicio de sesion");
        volverBtn.setOnAction(e -> {
            Scene escenaLogin = iniciarSesion.crearEscena(stage);
            App.cambiarEscena(escenaLogin, "Inicio de Sesión");
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.add(busesBtn, 0, 0);
        grid.add(paqueteriaBtn, 1, 0);
        grid.add(sedesBtn, 0, 1);
        grid.add(viajesBtn, 1, 1);
        grid.add(conductoresBtn, 0, 2);
        grid.add(pagosBtn, 1, 2);

        VBox layout = new VBox(10,
            titulo,
            grid,
            volverBtn
        );
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 600, 500);
        scene.getStylesheets().add(iniciarSesion.class.getResource("/styles.css").toExternalForm());
        return scene;
    }
}
