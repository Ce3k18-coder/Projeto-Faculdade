package com.faculdade.sistemachamados.config;

import com.faculdade.sistemachamados.model.Usuario;
import com.faculdade.sistemachamados.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DadosIniciais {

    @Value("${app.aluno.senha}")
    private String senhaAluno;

    @Value("${app.tecnico.senha}")
    private String senhaTecnico;

    @Bean
    public CommandLineRunner cadastrarUsuarios(UsuarioRepository repository) {
        return args -> {

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


            if (repository.findByEmail("vitor.verissimo@outlook.com").isEmpty()) {
                Usuario vitor = new Usuario(
                        "Vitor",
                        "vitor.verissimo@outlook.com",
                        encoder.encode(senhaAluno),
                        "ALUNO"
                );

                repository.save(vitor);
            }


            if (repository.findByEmail("heloisa.marinho@outlook.com").isEmpty()) {
                Usuario heloisa = new Usuario(
                        "Heloísa",
                        "heloisa.marinho@outlook.com",
                        encoder.encode(senhaAluno),
                        "ALUNO"
                );

                repository.save(heloisa);
            }


            if (repository.findByEmail("marlon.vitor@outlook.com").isEmpty()) {
                Usuario marlon = new Usuario(
                        "Marlon",
                        "marlon.vitor@outlook.com",
                        encoder.encode(senhaTecnico),
                        "TECNICO"
                );

                repository.save(marlon);
            }
        };
    }
}