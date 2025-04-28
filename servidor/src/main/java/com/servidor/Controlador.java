package com.servidor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.servidor.repository.insertarOperacionesRepository;
import com.servidor.repository.listarDatosRepository;
import com.servidor.repository.eliminarRegistrosRepository;

import java.util.*;

@RestController
@RequestMapping("/")
@CrossOrigin
public class Controlador {
    private final insertarOperacionesRepository operacionesRepository;
    private final listarDatosRepository listarDatosRepository;
    private final eliminarRegistrosRepository eliminarRepository;

    @Autowired
    public Controlador(insertarOperacionesRepository operacionesRepository,listarDatosRepository listarDatosRepository,
        eliminarRegistrosRepository eliminarRepository) {
        this.operacionesRepository = operacionesRepository;
        this.listarDatosRepository = listarDatosRepository;
        this.eliminarRepository = eliminarRepository;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String usuario = body.get("usuario");
        String clave = body.get("clave");

        if ("admin".equals(usuario) && "1234".equals(clave)) {
            return Map.of("token", "abc123");
        } else {
            return Map.of("error", "Credenciales inválidas");
        }
    }

    @PostMapping("/fechas-disponibles")
    public ResponseEntity<Object> fechasDisponibles(@RequestBody Map<String, String> body) {
        String origen = body.get("origen");
        String destino = body.get("destino");

        if (origen != null && destino != null && !origen.equals(destino)) {
            List<String> fechas = listarDatosRepository.listarFechasDisponibles(origen, destino);
            return ResponseEntity.ok(fechas);
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Datos inválidos"));
        }
    }

    @GetMapping("/listarOpcionesBuses")
    public List<String> listarBuses() {
        return listarDatosRepository.listarOpcionesBuses();
    }
    @GetMapping("/listarOpcionesSedes")
    public List<String> listarSedes() {
        return listarDatosRepository.listarOpcionesSedes();
    }

    @GetMapping("/busesRegistrados")
    public List<Map<String, Object>> obtenerBusesRegistrados() {
        return listarDatosRepository.listarBuses();
    }
    @GetMapping("/conductoresRegistrados")
    public List<String> obtenerConductoresRegistrados() {
        return listarDatosRepository.listarConductores();
    }
    @GetMapping("/pagosRegistrados")
    public List<Map<String, Object>> obtenerPagosRegistrados() {
        return listarDatosRepository.listarPagos();
    }
    @GetMapping("/pagosEnviosRegistrados")
    public List<Map<String, Object>> obtenerPagosEnviosRegistrados() {
        return listarDatosRepository.listarPagosEnvios();
    }
    @GetMapping("/paqueteriaRegistrada")
    public List<Map<String, Object>> obtenerPaqueteriaRegistrados() {
        return listarDatosRepository.listarPaqueteria();
    }
    @GetMapping("/sedesRegistradas")
    public List<Map<String, Object>> obtenerSedesRegistrados() {
        return listarDatosRepository.listarSedes();
    }
    @GetMapping("/viajesRegistrados")
    public List<Map<String, Object>> obtenerViajesRegistrados() {
        return listarDatosRepository.listarViajes();
    }


    @PostMapping("/borrarBus")
    public ResponseEntity<String> borrarBus(@RequestBody Map<String, String> body) {
        int id = Integer.parseInt(body.get("bus"));
        int resultado = eliminarRepository.eliminarBus(id);

        if (resultado > 0) {
            return ResponseEntity.ok("Sede eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar sede");
        }
    }
    @PostMapping("/borrarPaquete")
    public ResponseEntity<String> borrarPaquete(@RequestBody Map<String, String> body) {
        int id = Integer.parseInt(body.get("idPaqueteaquete"));
        int resultado = eliminarRepository.eliminarPaquete(id);

        if (resultado > 0) {
            return ResponseEntity.ok("Sede eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar sede");
        }
    }
    @PostMapping("/borrarSede")
    public ResponseEntity<String> borrarSede(@RequestBody Map<String, String> body) {
        String nombreSede = body.get("sede");
        int resultado = eliminarRepository.eliminarSede(nombreSede);

        if (resultado > 0) {
            return ResponseEntity.ok("Sede eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar sede");
        }
    }
    @PostMapping("/borrarViaje")
    public ResponseEntity<String> borrarViaje(@RequestBody Map<String, String> body) {
        int id = Integer.parseInt(body.get("viaje"));
        int resultado = eliminarRepository.eliminarViaje(id);

        if (resultado > 0) {
            return ResponseEntity.ok("Sede eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar sede");
        }
    }

    @PostMapping("/agregarSede")
    public ResponseEntity<String> agregarSede(@RequestBody Map<String, String> body) {
        String nombreSede = body.get("nombreSede");
        String direccion = body.get("direccion");
        int resultado = operacionesRepository.insertarSede(nombreSede,direccion);

        if (resultado > 0) {
            return ResponseEntity.ok("Sede agregada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al agregar sede");
        }
    }
    @PostMapping("/agregarBus")
    public ResponseEntity<String> agregarBus(@RequestBody Map<String, String> body) {
        String conductor = body.get("conductor");
        String empresa = body.get("empresa");
        String placa = body.get("placa");
        int aforo = Integer.parseInt(body.get("aforo"));
        int cantidadDePaquetes = Integer.parseInt(body.get("cantidadDePaquetes"));

        int resultado = operacionesRepository.insertarBus(conductor,empresa,placa,aforo,cantidadDePaquetes);
        if (resultado > 0) {
            return ResponseEntity.ok("Bus agregado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al agregar bus");
        }
    }
    @PostMapping("/agregarViaje")
    public String agregarViaje(@RequestBody Map<String, String> body) {
        String bus = body.get("bus");
        String fechaYhora = body.get("fechaYhora");
        String origen = body.get("origen");
        String destino = body.get("destino");
        int resultado = operacionesRepository.insertarViaje(bus, fechaYhora, origen, destino);

        if (resultado > 0) {
            return "Viaje agregado correctamente";
        } else {
            return "Error al agregar viaje";
        }
    }

    @PostMapping("/reservarViaje")
    public Map<String, String> reservarViaje(@RequestBody Map<String, Object> body) {
        int resultado = operacionesRepository.reservarViaje(body.get("datos"),body.get("infoPago"));
        if (resultado > 0) {
            return Map.of("mensaje", "Reserva procesada correctamente");
        } else {
            return Map.of("mensaje", "Reserva procesada correctamente");
        }
    }
    @PostMapping("/reservarEnvio")
    public Map<String, String> reservarEnvio(@RequestBody Map<String, Object> body) {
        int resultado = operacionesRepository.reservarEnvio(body.get("datos"),body.get("infoPago"));
        if (resultado > 0) {
            return Map.of("mensaje", "Reserva procesada correctamente");
        } else {
            return Map.of("mensaje", "Reserva procesada correctamente");
        }
    }
}
