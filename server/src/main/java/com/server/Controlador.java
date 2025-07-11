package com.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.server.repository.insertarOperacionesRepository;
import com.server.repository.listarDatosRepository;
import com.server.repository.eliminarRegistrosRepository;

import java.util.*;

@RestController
@RequestMapping("/")
@CrossOrigin
public class Controlador {
    private final insertarOperacionesRepository operacionesRepository;
    private final listarDatosRepository listarDatosRepository;
    private final eliminarRegistrosRepository eliminarRepository;
    private String adminToken;

    private boolean isAuthorized(String authHeader) {
        return authHeader != null && authHeader.equals("Bearer " + adminToken);
    }

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
            adminToken=UUID.randomUUID().toString();
            return Map.of("token", adminToken);
        } else {
            return Map.of("error", "Credenciales inválidas");
        }
    }

    @PostMapping("/fechasDisponibles")
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
    public ResponseEntity<?> obtenerBusesRegistrados(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthorized(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no autorizado"));
        }
        return ResponseEntity.ok(listarDatosRepository.listarBuses());
    }
    @GetMapping("/conductoresRegistrados")
    public ResponseEntity<?> obtenerConductoresRegistrados(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthorized(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no autorizado"));
        }
        return ResponseEntity.ok(listarDatosRepository.listarConductores());
    }
    @GetMapping("/pagosRegistrados")
    public ResponseEntity<?> obtenerPagosRegistrados(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthorized(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no autorizado"));
        }
        return ResponseEntity.ok(listarDatosRepository.listarPagos());
    }
    @GetMapping("/pagosEnviosRegistrados")
    public ResponseEntity<?> obtenerPagosEnviosRegistrados(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthorized(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no autorizado"));
        }
        return ResponseEntity.ok(listarDatosRepository.listarPagosEnvios());
    }
    @GetMapping("/paqueteriaRegistrada")
    public ResponseEntity<?> obtenerPaqueteriaRegistrada(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthorized(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no autorizado"));
        }
        return ResponseEntity.ok(listarDatosRepository.listarPaqueteria());
    }
    @GetMapping("/sedesRegistradas")
    public ResponseEntity<?> obtenerSedesRegistradas(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthorized(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no autorizado"));
        }
        return ResponseEntity.ok(listarDatosRepository.listarSedes());
    }
    @GetMapping("/viajesRegistrados")
    public ResponseEntity<?> obtenerViajesRegistrados(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthorized(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no autorizado"));
        }
        return ResponseEntity.ok(listarDatosRepository.listarViajes());
    }

    @PostMapping("/borrarBus")
    public ResponseEntity<String> borrarBus(
        @RequestHeader(value="Authorization",required=false) String authHeader,
        @RequestBody Map<String, String> body) {
            if (!isAuthorized(authHeader)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autorizado");
            }

            String id = body.get("bus");
            int resultado = eliminarRepository.eliminarBus(id);
            if (resultado > 0) {
                return ResponseEntity.ok("Sede eliminada correctamente");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar sede");
            }
    }
    @PostMapping("/borrarPaquete")
    public ResponseEntity<String> borrarPaquete(
        @RequestHeader(value="Authorization",required=false) String authHeader,
        @RequestBody Map<String, String> body) {
            if (!isAuthorized(authHeader)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autorizado");
            }

            int id = Integer.parseInt(body.get("idPaquete"));
            int resultado = eliminarRepository.eliminarPaquete(id);
            if (resultado > 0) {
                return ResponseEntity.ok("Sede eliminada correctamente");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar sede");
            }
    }
    @PostMapping("/borrarSede")
    public ResponseEntity<String> borrarSede(
        @RequestHeader(value="Authorization",required=false) String authHeader,
        @RequestBody Map<String, String> body) {
            if (!isAuthorized(authHeader)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autorizado");
            }

            String nombreSede = body.get("sede");
            int resultado = eliminarRepository.eliminarSede(nombreSede);
            if (resultado > 0) {
                return ResponseEntity.ok("Sede eliminada correctamente");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar sede");
            }
    }
    @PostMapping("/borrarViaje")
    public ResponseEntity<String> borrarViaje(
        @RequestHeader(value="Authorization",required=false) String authHeader,
        @RequestBody Map<String, String> body) {
            if (!isAuthorized(authHeader)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autorizado");
            }

            int id = Integer.parseInt(body.get("viaje"));
            int resultado = eliminarRepository.eliminarViaje(id);
            if (resultado > 0) {
                return ResponseEntity.ok("Sede eliminada correctamente");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar sede");
            }
    }

    @PostMapping("/agregarSede")
    public ResponseEntity<String> agregarSede(
        @RequestHeader(value="Authorization",required=false) String authHeader,
        @RequestBody Map<String, String> body) {
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
    public ResponseEntity<String> agregarBus(
        @RequestHeader(value="Authorization",required=false) String authHeader,
        @RequestBody Map<String, String> body) {
            if (!isAuthorized(authHeader)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autorizado");
            }

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
    public ResponseEntity<Map<String,String>> agregarViaje(
        @RequestHeader(value="Authorization",required=false) String authHeader,
        @RequestBody Map<String, String> body) {
            if (!isAuthorized(authHeader)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Usuario no autorizado"));
            }

            String bus = body.get("bus");
            String fechaYhora = body.get("fechaYhora");
            String origen = body.get("origen");
            String destino = body.get("destino");
            int resultado = operacionesRepository.insertarViaje(bus, fechaYhora, origen, destino);
            if (resultado > 0) {
                return ResponseEntity.ok(Map.of("mensaje", "Viaje agregado correctamente"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al agregar viaje"));
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
