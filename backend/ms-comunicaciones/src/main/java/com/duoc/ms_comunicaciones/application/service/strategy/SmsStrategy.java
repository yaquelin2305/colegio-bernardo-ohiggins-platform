package com.duoc.ms_comunicaciones.application.service.strategy;

import com.duoc.ms_comunicaciones.domain.model.Comunicacion;
import com.duoc.ms_comunicaciones.domain.model.Canal;
import com.duoc.ms_comunicaciones.domain.port.in.ComunicacionStrategy;
import org.springframework.stereotype.Component;

@Component
public class SmsStrategy implements ComunicacionStrategy {
    @Override
    public void dispatch(Comunicacion comunicacion) {
        // Aquí iría la lógica de un proveedor de SMS como Twilio
        System.out.println("Enviando SMS a: " + comunicacion.getDestinatario());
        System.out.println("Mensaje: " + comunicacion.getMensaje());
    }

    @Override
    public boolean supports(Canal canal) {
        return Canal.SMS.equals(canal);
    }
}