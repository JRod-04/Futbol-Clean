package com.futbol.estadisticas;

import com.futbol.estadisticas.application.port.out.UsuarioRepositoryPort;
import com.futbol.estadisticas.domain.model.Usuario;
import com.futbol.estadisticas.domain.model.enums.Rol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@SpringBootApplication
public class EstadisticasApplication {

	public static void main(String[] args) {
		SpringApplication.run(EstadisticasApplication.class, args);
	}

}
