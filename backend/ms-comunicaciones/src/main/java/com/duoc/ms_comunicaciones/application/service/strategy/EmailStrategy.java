package com.duoc.ms_comunicaciones.application.service.strategy;

import com.duoc.ms_comunicaciones.domain.model.Comunicacion;
import com.duoc.ms_comunicaciones.domain.model.Canal;
import com.duoc.ms_comunicaciones.domain.port.in.ComunicacionStrategy;
import org.springframework.stereotype.Component;

@Component
public class EmailStrategy implements ComunicacionStrategy {
    @Override
    public void dispatch(Comunicacion comunicacion) {
        // Aquí iría la lógica real de JavaMailSender
        System.out.println("Enviando EMAIL a: " + comunicacion.getDestinatario());
        System.out.println("Asunto: " + comunicacion.getAsunto());
    }

    @Override
    public boolean supports(Canal canal) {
        return Canal.EMAIL.equals(canal);
    }
}