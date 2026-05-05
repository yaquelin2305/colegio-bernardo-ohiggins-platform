package com.duoc.ms_comunicaciones.domain.port.in;

import com.duoc.ms_comunicaciones.domain.model.Comunicacion;
import com.duoc.ms_comunicaciones.domain.model.Canal;

public interface ComunicacionStrategy {
    void dispatch(Comunicacion comunicacion);
    boolean supports(Canal canal);
}