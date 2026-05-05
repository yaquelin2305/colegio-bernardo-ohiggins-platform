package com.duoc.ms_comunicaciones.domain.port.out;

import com.duoc.ms_comunicaciones.domain.model.Comunicacion;
import java.util.List;
import java.util.Optional;

public interface ComunicacionRepositoryPort {
    
    
    Comunicacion save(Comunicacion comunicacion);
    
   
    List<Comunicacion> findByUsuarioId(String usuarioId);
    
   
    Optional<Comunicacion> findById(Long id);
    
   
    Comunicacion updateLeido(Long id, boolean leido);
}