package com.cbo.bff.comunicaciones.infrastructure.input.rest;

import com.cbo.bff.comunicaciones.application.service.ComunicacionBffService;
import com.cbo.bff.comunicaciones.domain.dto.DestinatarioDTO;
import com.cbo.bff.comunicaciones.domain.dto.EnviarMensajeRequestDTO;
import com.cbo.bff.comunicaciones.domain.dto.MensajeBffDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bff/comunicaciones")
@RequiredArgsConstructor
public class ComunicacionBffController {

    private final ComunicacionBffService comunicacionBffService;

    @GetMapping("/bandeja/{usuarioId}")
    public ResponseEntity<List<MensajeBffDTO>> getBandeja(@PathVariable String usuarioId) {
        return ResponseEntity.ok(comunicacionBffService.getBandeja(usuarioId));
    }

    @GetMapping("/{mensajeId}")
    public ResponseEntity<MensajeBffDTO> getMensaje(@PathVariable Long mensajeId) {
        return ResponseEntity.ok(comunicacionBffService.getMensaje(mensajeId));
    }

    @PostMapping("/enviar")
    public ResponseEntity<MensajeBffDTO> enviarMensaje(@RequestBody EnviarMensajeRequestDTO request) {
        return ResponseEntity.ok(comunicacionBffService.enviarMensaje(request));
    }

    @GetMapping("/destinatarios")
    public ResponseEntity<List<DestinatarioDTO>> getDestinatarios(
            @RequestHeader("X-User-Uuid") String currentUserUuid) {
        return ResponseEntity.ok(comunicacionBffService.getDestinatarios(currentUserUuid));
    }

    @PatchMapping("/leido/{mensajeId}")
    public ResponseEntity<MensajeBffDTO> marcarLeido(@PathVariable Long mensajeId) {
        return ResponseEntity.ok(comunicacionBffService.marcarLeido(mensajeId));
    }
}
