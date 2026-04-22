package com.colegio.notificacion.infrastructure.api;

import com.colegio.notificacion.application.usecase.NotificacionService;
import com.colegio.notificacion.domain.model.Notificacion;
import com.colegio.notificacion.infrastructure.api.dto.NotificacionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {
    private final NotificacionService notificacionService;

    @PostMapping
    public ResponseEntity<Notificacion> enviar(@RequestBody NotificacionDTO dto) {
        Notificacion notificacion = new Notificacion();
        notificacion.setEmisor(dto.getEmisor());
        notificacion.setDestinatario(dto.getDestinatario());
        notificacion.setAsunto(dto.getAsunto());
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setFechaEnvio(dto.getFechaEnvio());
        
        return ResponseEntity.ok(notificacionService.crearNotificacion(notificacion));
    }

    @GetMapping
    public ResponseEntity<List<Notificacion>> listar() {
        return ResponseEntity.ok(notificacionService.listarNotificaciones());
    }
}