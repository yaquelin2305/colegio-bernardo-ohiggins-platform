package com.cbo.bff.comunicaciones.application.service;

import com.cbo.bff.comunicaciones.domain.dto.DestinatarioDTO;
import com.cbo.bff.comunicaciones.domain.dto.EnviarMensajeRequestDTO;
import com.cbo.bff.comunicaciones.domain.dto.MensajeBffDTO;
import com.cbo.bff.comunicaciones.infrastructure.output.feign.ComunicacionFeignClient;
import com.cbo.bff.comunicaciones.infrastructure.output.feign.dto.ComunicacionMsRequestDTO;
import com.cbo.bff.comunicaciones.infrastructure.output.feign.dto.ComunicacionMsResponseDTO;
import com.cbo.bff.gestionacademica.infrastructure.output.feign.UsuarioFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ComunicacionBffService {

    private final ComunicacionFeignClient comunicacionFeignClient;
    private final UsuarioFeignClient usuarioFeignClient;

    public List<MensajeBffDTO> getBandeja(String usuarioId) {
        return comunicacionFeignClient.getBandeja(usuarioId)
                .stream()
                .map(this::toMensajeBffDTO)
                .toList();
    }

    public MensajeBffDTO getMensaje(Long mensajeId) {
        return toMensajeBffDTO(comunicacionFeignClient.getMensaje(mensajeId));
    }

    public MensajeBffDTO enviarMensaje(EnviarMensajeRequestDTO request) {
        ComunicacionMsRequestDTO msRequest = ComunicacionMsRequestDTO.builder()
                .destinatario(request.getDestinatario())
                .asunto(request.getAsunto())
                .mensaje(request.getMensaje())
                .canal(request.getCanal())
                .tipo(request.getTipo())
                .build();
        return toMensajeBffDTO(comunicacionFeignClient.enviar(msRequest));
    }

    public List<DestinatarioDTO> getDestinatarios(String currentUserUuid) {
        Map<String, String> etiquetas = Map.of(
                "APODERADO", "Apoderado",
                "DOCENTE",   "Docente",
                "ADMIN",     "Administrador"
        );
        return Stream.of("APODERADO", "DOCENTE", "ADMIN")
                .flatMap(rol -> usuarioFeignClient.listarPorRol(rol).stream()
                        .filter(u -> !currentUserUuid.equals(String.valueOf(u.get("id"))))
                        .map(u -> {
                            String nombre = (String) u.get("nombreCompleto");
                            return new DestinatarioDTO(nombre, nombre + " - " + etiquetas.get(rol));
                        })
                )
                .toList();
    }

    public MensajeBffDTO marcarLeido(Long mensajeId) {
        return toMensajeBffDTO(comunicacionFeignClient.marcarLeido(mensajeId));
    }

    private MensajeBffDTO toMensajeBffDTO(ComunicacionMsResponseDTO ms) {
        return MensajeBffDTO.builder()
                .id(ms.getMensajeId())
                .remitente(ms.getUsuarioId())
                .asunto(ms.getAsunto())
                .cuerpo(ms.getMensaje())
                .canal(ms.getCanal())
                .tipo(ms.getTipo())
                .fecha(ms.getFechaEnvio())
                .leido(ms.isLeido())
                .build();
    }
}
