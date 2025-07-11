package com.front.app;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SceneIniciarSesion {
    public static String adminToken = null;

    public static String respuesta(String usuario, String clave) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(new LoginData(usuario, clave));
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Config.URL_BACKEND+"/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                if (response.body().contains("token")) {
                    String token = mapper.readTree(response.body()).get("token").asText();
                    adminToken = token;
                    return "login_exitoso";
                } else {
                    return "credenciales_incorrectas";
                }
            } else {
                return "error_conexion";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error_conexion";
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
                String resultado = respuesta(usuario, clave);
                Platform.runLater(() -> {
                    switch (resultado) {
                        case "login_exitoso":
                            mensaje.setText("Inicio de sesión exitoso.");
                            Scene escenaPaginaPrincipal = ScenePaginaPrincipal.crearEscena(stage);
                            stage.setScene(escenaPaginaPrincipal);
                            stage.setTitle("Página Principal");
                            break;
                        case "credenciales_incorrectas":
                            mensaje.setText("Usuario o contraseña incorrectos.");
                            break;
                        case "error_conexion":
                            mensaje.setText("Error de conexión. Verifica tu conexión a internet o conexion al server.");
                            break;
                        default:
                            mensaje.setText("Ocurrió un error desconocido.");
                    }
                });
            }).start();
        });

        Button volverBtn = new Button("Volver al inicio");
        volverBtn.setOnAction(e -> {
            Scene escenaInicio = SceneInicio.crearEscena(stage);
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
        scene.getStylesheets().add(SceneIniciarSesion.class.getResource("/styles.css").toExternalForm());
        return scene;
    }
}
