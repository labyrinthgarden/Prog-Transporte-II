package com.ejemplo.servidor;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/")
@CrossOrigin
public class Controlador {

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String usuario = body.get("usuario");
        String clave = body.get("clave");

        System.out.println("Intento de login: " + usuario + ", " + clave);

        if ("admin".equals(usuario) && "1234".equals(clave)) {
            return Map.of("token", "abc123");
        } else {
            return Map.of("error", "Credenciales inválidas");
        }
    }

    @PostMapping("/fechas-disponibles")
    public Object fechasDisponibles(@RequestBody Map<String, String> body) {
        String origen = body.get("origen");
        String destino = body.get("destino");

        if (origen != null && destino != null && !origen.equals(destino)) {
            return Map.of("fechas", List.of("2025-04-12", "2025-04-14", "2025-04-18"));
        } else {
            return Map.of("error", "Datos inválidos");
        }
    }

    @GetMapping("/buses-registrados")
    public List<String> obtenerBusesRegistrados() {
        return List.of("Bus 1 - Empresa A - Conductor","Bus 2 - Empresa B - conductor","Bus 3 - Empresa C - conductor");

    }

    @GetMapping("/conductores-registrados")
    public List<String> obtenerconductoresRegistrados() {
        return List.of("conductor1","conductor2","conductor3");

    }

    @PostMapping("/borrar-bus")
    public String borrarBus(@RequestBody Map<String, String> body) {
        String bus = body.get("bus");
        // lógica para eliminar
        return "Bus eliminado: " + bus;
    }

    @PostMapping("/borrar-conductor")
    public String borrarConductor(@RequestBody Map<String, String> body) {
        String bus = body.get("conductor");
        // lógica para eliminar
        return "conductor eliminado: " + bus;
    }

    @PostMapping("/reservarViaje")
    public Map<String, String> reservarViaje(@RequestBody List<Object> datos) {
        System.out.println("Reserva Viaje recibida:");
        System.out.println("Nombre: " + datos.get(0));
        System.out.println("ID: " + datos.get(1));
        System.out.println("Origen: " + datos.get(2));
        System.out.println("Destino: " + datos.get(3));
        System.out.println("Fecha: " + datos.get(4));
        System.out.println("Con equipaje: " + datos.get(5));
        System.out.println("Cantidad equipaje: " + datos.get(6));
        System.out.println("Peso equipaje: " + datos.get(7));

        return Map.of("mensaje", "Reserva procesada correctamente");
    }

    @PostMapping("/reservarEnvio")
    public Map<String, String> reservarEnvio(@RequestBody List<Object> datos) {
        System.out.println("Reserva envio recibida:");
        System.out.println("Nombre Remitente: " + datos.get(0));
        System.out.println("ID Remitente: " + datos.get(1));
        System.out.println("Nombre Destinatario: " + datos.get(2));
        System.out.println("ID Destinatario: " + datos.get(3));
        System.out.println("Sede envio: " + datos.get(4));
        System.out.println("Sede entrega: " + datos.get(5));
        System.out.println("Peso paquete: " + datos.get(6));

        return Map.of("mensaje", "Reserva procesada correctamente");
    }
}
