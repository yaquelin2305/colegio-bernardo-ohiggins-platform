package com.duoc.ms_asistencia.aplicacion.config;

import com.duoc.ms_asistencia.dominio.strategy.EstrategiaAsistencia;
import com.duoc.ms_asistencia.dominio.strategy.EstrategiaPresente;
import com.duoc.ms_asistencia.dominio.strategy.EstrategiaAusente;
import com.duoc.ms_asistencia.dominio.strategy.EstrategiaJustificado;
import com.duoc.ms_asistencia.dominio.entity.EstadoAsistencia;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class EstrategiaConfig {

    @Bean
    public Map<EstadoAsistencia, EstrategiaAsistencia> estrategiaMap() {
        Map<EstadoAsistencia, EstrategiaAsistencia> map = new HashMap<>();
        map.put(EstadoAsistencia.PRESENTE, new EstrategiaPresente());
        map.put(EstadoAsistencia.AUSENTE, new EstrategiaAusente());
        map.put(EstadoAsistencia.JUSTIFICADO, new EstrategiaJustificado());
        return map;
    }
}