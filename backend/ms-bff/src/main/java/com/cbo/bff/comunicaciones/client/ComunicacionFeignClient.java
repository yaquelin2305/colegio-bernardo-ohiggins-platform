package com.cbo.bff.comunicaciones.client;

import com.cbo.bff.comunicaciones.dto.ms.ComunicacionMsRequestDTO;
import com.cbo.bff.comunicaciones.dto.ms.ComunicacionMsResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "ms-comunicaciones")
public interface ComunicacionFeignClient {

    @GetMapping("/api/comunicaciones/bandeja/{usuarioId}")
    List<ComunicacionMsResponseDTO> getBandeja(@PathVariable String usuarioId);

    @GetMapping("/api/comunicaciones/{mensajeId}")
    ComunicacionMsResponseDTO getMensaje(@PathVariable Long mensajeId);

    @PostMapping("/api/comunicaciones/enviar")
    ComunicacionMsResponseDTO enviar(@RequestBody ComunicacionMsRequestDTO request);

    @GetMapping("/api/comunicaciones/destinatarios")
    List<String> getDestinatarios();

    @PatchMapping("/api/comunicaciones/leido/{mensajeId}")
    ComunicacionMsResponseDTO marcarLeido(@PathVariable Long mensajeId);
}
