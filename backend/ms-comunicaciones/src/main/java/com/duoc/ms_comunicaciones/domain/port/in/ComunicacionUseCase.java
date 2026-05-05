package com.duoc.ms_comunicaciones.domain.port.in;

import com.duoc.ms_comunicaciones.domain.model.Comunicacion;
import java.util.List;
import java.util.Optional;

public interface ComunicacionUseCase {
    
    
    Comunicacion enviar(Comunicacion comunicacion);
    
   
    List<Comunicacion> getBandeja(String usuarioId);
    
    
    Optional<Comunicacion> getMensaje(Long mensajeId);
    
   
    Comunicacion marcarLeido(Long mensajeId);
}