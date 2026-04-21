package com.colegio.notificacion;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotificacionApplication {

    public static void main(String[] args) {
      
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing() 
                .load();
        
       
        String dbPass = dotenv.get("DB_PASSWORD");
        if (dbPass != null) {
            System.setProperty("DB_PASSWORD", dbPass);
        }

        SpringApplication.run(NotificacionApplication.class, args);
    }
}