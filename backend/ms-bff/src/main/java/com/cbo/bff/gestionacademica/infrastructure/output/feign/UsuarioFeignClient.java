package com.cbo.bff.gestionacademica.infrastructure.output.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "ms-usuario")
public interface UsuarioFeignClient {

    @GetMapping("/api/v1/admin/listar/{rol}")
    List<Map<String, Object>> listarPorRol(@PathVariable("rol") String rol);

    @GetMapping("/api/v1/admin/{id}")
    Map<String, Object> obtenerPorId(@PathVariable("id") UUID id);

    @GetMapping("/api/v1/usuarios/{uuid}/nombre")
    Map<String, Object> obtenerNombre(@PathVariable("uuid") String uuid);
}
