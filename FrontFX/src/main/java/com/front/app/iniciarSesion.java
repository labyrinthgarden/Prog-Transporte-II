package com.front.app;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
//import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class iniciarSesion {
    public static boolean respuesta(String usuario, String clave) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(new LoginData(usuario, clave));
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 && response.body().contains("token");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static class LoginData {
        public String usuario;
        public String clave;
        public LoginData(String usuario, String clave) {
            this.usuario = usuario;
            this.clave = clave;
        }
    }
    public static Scene crearEscena(Stage stage) {
        Label titulo = new Label("Inicia Sesión");
        titulo.setId("titulo");

        TextField entradaUsuario = new TextField();
        entradaUsuario.setPromptText("Usuario");

        PasswordField entradaPasswd = new PasswordField();
        entradaPasswd.setPromptText("Contraseña");

        Button iniciarSesionBtn = new Button("Iniciar Sesión");

        Label mensaje = new Label();
        mensaje.setId("feedback");

        iniciarSesionBtn.setOnAction(e -> {
            String usuario = entradaUsuario.getText();
            String clave = entradaPasswd.getText();
            if (usuario.isEmpty() || clave.isEmpty()) {
                mensaje.setText("Completa todos los campos.");
                return;
            }
            new Thread(() -> {
                boolean loginExitoso = respuesta(usuario, clave);
                Platform.runLater(() -> {
                    if (loginExitoso) {
                        mensaje.setText("Inicio de sesión exitoso.");
                        Scene escenaPaginaPrincipal = paginaPrincipal.crearEscena(stage);
                        stage.setScene(escenaPaginaPrincipal);
                        stage.setTitle("Página Principal");
                    } else {
                        mensaje.setText("Usuario o contraseña incorrectos.");
                    }
                });
            }).start();
        });

        Button volverBtn = new Button("Volver al inicio");
        volverBtn.setOnAction(e -> {
            Scene escenaInicio = inicio.crearEscena(stage);
            App.cambiarEscena(escenaInicio, "Inicio");
        });

        VBox formulario = new VBox(20,
                titulo,
                entradaUsuario,
                entradaPasswd,
                iniciarSesionBtn,
                volverBtn,
                mensaje
        );
        formulario.setPadding(new Insets(20));
        formulario.setAlignment(Pos.CENTER);

        Scene scene = new Scene(formulario, 600, 500);
        scene.getStylesheets().add(iniciarSesion.class.getResource("/styles.css").toExternalForm());
        return scene;
    }
}
