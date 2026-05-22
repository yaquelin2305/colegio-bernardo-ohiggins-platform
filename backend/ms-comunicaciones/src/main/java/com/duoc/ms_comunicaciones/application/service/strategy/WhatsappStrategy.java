package com.duoc.ms_comunicaciones.application.service.strategy;

import com.duoc.ms_comunicaciones.domain.model.Comunicacion;
import com.duoc.ms_comunicaciones.domain.model.Canal;
import com.duoc.ms_comunicaciones.domain.port.in.ComunicacionStrategy;
import org.springframework.stereotype.Component;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class WhatsappStrategy implements ComunicacionStrategy {

    @Override
    public void dispatch(Comunicacion comunicacion) {
     
        
        String telefono = comunicacion.getDestinatario(); 
        String mensajeRaw = "Asunto: " + comunicacion.getAsunto() + " - " + comunicacion.getMensaje();
        
       
        String mensajeEncoded = URLEncoder.encode(mensajeRaw, StandardCharsets.UTF_8);
        
        String linkWhatsapp = "https://wa.me/" + telefono + "?text=" + mensajeEncoded;

        System.out.println("=== SIMULACIÓN WHATSAPP ===");
        System.out.println("Generando enlace de envío: " + linkWhatsapp);
        System.out.println("===========================");
        
       
    }

    @Override
    public boolean supports(Canal canal) {
       
        return Canal.WHATSAPP.equals(canal);
    }
}