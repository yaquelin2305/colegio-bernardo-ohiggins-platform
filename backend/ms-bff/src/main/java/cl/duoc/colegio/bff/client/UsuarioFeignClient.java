package cl.duoc.colegio.bff.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

/**
 * Feign Client → MS-Usuario.
 * Rutas alineadas con el refactor de AuthController.
 */
@FeignClient(name = "ms-usuario", url = "${feign.usuario.url:}")
public interface UsuarioFeignClient {

    /**
     * Listar usuarios por rol.
     * Ruta: GET /api/v1/admin/listar/{rol}
     */
    @GetMapping("/api/v1/admin/listar/{rol}")
    List<Map<String, Object>> listarPorRol(@PathVariable("rol") String rol);
}
