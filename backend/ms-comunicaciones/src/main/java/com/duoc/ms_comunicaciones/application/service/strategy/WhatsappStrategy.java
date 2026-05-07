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
        // En una API real aquí llamarías a Meta. 
        // Aquí simulamos creando el enlace profesional (Click to Chat)
        
        String telefono = comunicacion.getDestinatario(); // Ej: 56912345678
        String mensajeRaw = "Asunto: " + comunicacion.getAsunto() + " - " + comunicacion.getMensaje();
        
        // Codificamos el texto para que sea válido en una URL (cambia espacios por %20, etc.)
        String mensajeEncoded = URLEncoder.encode(mensajeRaw, StandardCharsets.UTF_8);
        
        String linkWhatsapp = "https://wa.me/" + telefono + "?text=" + mensajeEncoded;

        System.out.println("=== SIMULACIÓN WHATSAPP ===");
        System.out.println("Generando enlace de envío: " + linkWhatsapp);
        System.out.println("===========================");
        
        // Podríamos incluso guardar este link en un campo extra si quisiéramos, 
        // pero por ahora lo dejamos como la acción de la estrategia.
    }

    @Override
    public boolean supports(Canal canal) {
        // Debes agregar WHATSAPP a tu Enum Canal para que esto no de error
        return Canal.WHATSAPP.equals(canal);
    }
}