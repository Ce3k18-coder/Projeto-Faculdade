
package com.faculdade.sistemachamados.config;

import com.faculdade.sistemachamados.model.Usuario;
import com.faculdade.sistemachamados.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DadosIniciais {

    @Bean
    public CommandLineRunner cadastrarUsuarios(UsuarioRepository repository) {
        return args -> {

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            if (repository.findByEmail("aluno@teste.com").isEmpty()) {
                Usuario aluno = new Usuario(
                        "Aluno Teste",
                        "aluno@teste.com",
                        encoder.encode("Aluno@123"),
                        "ALUNO"
                );

                repository.save(aluno);
            }

            if (repository.findByEmail("tecnico@teste.com").isEmpty()) {
                Usuario tecnico = new Usuario(
                        "Tecnico Teste",
                        "tecnico@teste.com",
                        encoder.encode("Tecnico@123"),
                        "TECNICO"
                );

                repository.save(tecnico);
            }
        };
    }
}
