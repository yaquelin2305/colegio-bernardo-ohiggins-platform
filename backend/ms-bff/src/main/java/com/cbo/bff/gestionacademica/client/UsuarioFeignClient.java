package com.cbo.bff.gestionacademica.client;

import com.cbo.bff.gestionacademica.dto.ms.UsuarioMsDTO;
import com.cbo.bff.gestionacademica.dto.ms.UsuarioNombreMsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "ms-usuario")
public interface UsuarioFeignClient {

    @GetMapping("/api/v1/admin/listar/{rol}")
    List<UsuarioMsDTO> listarPorRol(@PathVariable("rol") String rol);

    @GetMapping("/api/v1/admin/{id}")
    UsuarioMsDTO obtenerPorId(@PathVariable("id") UUID id);

    @GetMapping("/api/v1/usuarios/{uuid}/nombre")
    UsuarioNombreMsDTO obtenerNombre(@PathVariable("uuid") String uuid);
}
