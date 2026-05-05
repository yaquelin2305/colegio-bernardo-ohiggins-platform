package com.duoc.ms_comunicaciones.infrastructure.adapter.in.rest;
import com.duoc.ms_comunicaciones.domain.model.Comunicacion;
import com.duoc.ms_comunicaciones.domain.model.Canal;
import com.duoc.ms_comunicaciones.domain.port.in.ComunicacionUseCase;
import com.duoc.ms_comunicaciones.infrastructure.adapter.in.rest.dto.ComunicacionRequestDTO;
import com.duoc.ms_comunicaciones.infrastructure.adapter.in.rest.dto.ComunicacionResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comunicaciones")
public class ComunicacionController {

    private final ComunicacionUseCase useCase;

    public ComunicacionController(ComunicacionUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/enviar")
    public ResponseEntity<ComunicacionResponseDTO> enviar(@RequestBody ComunicacionRequestDTO request) {
       
        Comunicacion comunicacion = Comunicacion.builder()
                .usuarioId(request.getDestinatario()) 
                .destinatario(request.getDestinatario())
                .asunto(request.getAsunto())
                .mensaje(request.getMensaje())
                .canal(Canal.valueOf(request.getCanal().toUpperCase()))
                .fechaEnvio(LocalDateTime.now())
                .leido(false)
                .build();

        Comunicacion guardada = useCase.enviar(comunicacion);
        return ResponseEntity.ok(toResponseDTO(guardada));
    }

    @GetMapping("/bandeja/{usuarioId}")
    public ResponseEntity<List<ComunicacionResponseDTO>> getBandeja(@PathVariable String usuarioId) {
        List<ComunicacionResponseDTO> lista = useCase.getBandeja(usuarioId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PatchMapping("/leido/{mensajeId}")
    public ResponseEntity<ComunicacionResponseDTO> marcarLeido(@PathVariable Long mensajeId) {
        Comunicacion actualizada = useCase.marcarLeido(mensajeId);
        return ResponseEntity.ok(toResponseDTO(actualizada));
    }

   
    private ComunicacionResponseDTO toResponseDTO(Comunicacion dom) {
        ComunicacionResponseDTO dto = new ComunicacionResponseDTO();
        dto.setMensajeId(dom.getId());
        dto.setUsuarioId(dom.getUsuarioId());
        dto.setDestinatario(dom.getDestinatario());
        dto.setAsunto(dom.getAsunto());
        dto.setMensaje(dom.getMensaje());
        dto.setCanal(dom.getCanal().name());
        dto.setFechaEnvio(dom.getFechaEnvio());
        dto.setLeido(dom.isLeido());
        return dto;
    }
}