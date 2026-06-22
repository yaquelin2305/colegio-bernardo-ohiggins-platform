package com.cbo.bff.comunicaciones.service;

import com.cbo.bff.comunicaciones.dto.DestinatarioDTO;
import com.cbo.bff.comunicaciones.dto.EnviarMensajeRequestDTO;
import com.cbo.bff.comunicaciones.dto.MensajeBffDTO;
import com.cbo.bff.comunicaciones.client.ComunicacionFeignClient;
import com.cbo.bff.comunicaciones.dto.ms.ComunicacionMsRequestDTO;
import com.cbo.bff.comunicaciones.dto.ms.ComunicacionMsResponseDTO;
import com.cbo.bff.gestionacademica.client.UsuarioFeignClient;
import com.cbo.bff.gestionacademica.dto.ms.UsuarioMsDTO;
import com.cbo.bff.gestionacademica.dto.ms.UsuarioNombreMsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ComunicacionBffService {

    private final ComunicacionFeignClient comunicacionFeignClient;
    private final UsuarioFeignClient usuarioFeignClient;

    public List<MensajeBffDTO> getBandeja(String usuarioId) {
        Map<String, String> cache = new HashMap<>();
        return comunicacionFeignClient.getBandeja(usuarioId)
                .stream()
                .map(ms -> toMensajeBffDTO(ms, cache))
                .toList();
    }

    public MensajeBffDTO getMensaje(Long mensajeId) {
        return toMensajeBffDTO(comunicacionFeignClient.getMensaje(mensajeId), new HashMap<>());
    }

    public MensajeBffDTO enviarMensaje(EnviarMensajeRequestDTO request, String remitenteUuid) {
        ComunicacionMsRequestDTO msRequest = ComunicacionMsRequestDTO.builder()
                .usuarioId(remitenteUuid)
                .destinatario(request.getDestinatario())
                .asunto(request.getAsunto())
                .mensaje(request.getMensaje())
                .canal(request.getCanal())
                .tipo(request.getTipo())
                .build();
        MensajeBffDTO dto = toMensajeBffDTO(comunicacionFeignClient.enviar(msRequest), new HashMap<>());
        String nombreRemitente = resolverNombreRemitente(remitenteUuid);
        return MensajeBffDTO.builder()
                .id(dto.getId())
                .remitente(nombreRemitente != null ? nombreRemitente : remitenteUuid)
                .asunto(dto.getAsunto())
                .cuerpo(dto.getCuerpo())
                .canal(dto.getCanal())
                .tipo(dto.getTipo())
                .fecha(dto.getFecha())
                .leido(dto.isLeido())
                .build();
    }

    public List<DestinatarioDTO> getDestinatarios(String currentUserUuid) {
        Map<String, String> etiquetas = Map.of(
                "APODERADO", "Apoderado",
                "DOCENTE",   "Docente",
                "ADMIN",     "Administrador"
        );
        return Stream.of("APODERADO", "DOCENTE", "ADMIN")
                .flatMap(rol -> usuarioFeignClient.listarPorRol(rol).stream()
                        .filter(u -> !currentUserUuid.equals(u.getId()))
                        .map(u -> {
                            String nombre = u.getNombreCompleto();
                            return new DestinatarioDTO(u.getId(), nombre + " - " + etiquetas.get(rol));
                        })
                )
                .toList();
    }

    public MensajeBffDTO marcarLeido(Long mensajeId) {
        return toMensajeBffDTO(comunicacionFeignClient.marcarLeido(mensajeId), new HashMap<>());
    }

    private MensajeBffDTO toMensajeBffDTO(ComunicacionMsResponseDTO ms, Map<String, String> cache) {
        String uuid = ms.getUsuarioId();
        String remitente = cache.computeIfAbsent(uuid, this::resolverNombreRemitente);
        return MensajeBffDTO.builder()
                .id(ms.getMensajeId())
                .remitente(remitente != null ? remitente : uuid)
                .asunto(ms.getAsunto())
                .cuerpo(ms.getMensaje())
                .canal(ms.getCanal())
                .tipo(ms.getTipo())
                .fecha(ms.getFechaEnvio())
                .leido(ms.isLeido())
                .build();
    }

    private String resolverNombreRemitente(String uuid) {
        if (uuid == null) return null;
        if ("00000000-0000-0000-0000-000000000000".equals(uuid)) return "Sistema";
        try {
            UsuarioNombreMsDTO res = usuarioFeignClient.obtenerNombre(uuid);
            return res != null ? res.getNombreCompleto() : null;
        } catch (Exception ex) {
            return null;
        }
    }
}
