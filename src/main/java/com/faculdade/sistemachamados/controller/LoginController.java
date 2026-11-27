package com.faculdade.sistemachamados.controller;

import com.faculdade.sistemachamados.model.Usuario;
import com.faculdade.sistemachamados.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import java.time.Duration;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Controller
public class LoginController {

    private final UsuarioRepository usuarioRepository;
    private final Environment environment;
    private final BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder();

    public LoginController(
            UsuarioRepository usuarioRepository,
            Environment environment) {

        this.usuarioRepository = usuarioRepository;
        this.environment = environment;
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam(value = "remember-me", required = false)
            String lembrarMe,
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioEncontrado =
                usuarioRepository.findByEmail(email);

        if (usuarioEncontrado.isEmpty() ||
                !encoder.matches(senha, usuarioEncontrado.get().getSenha())) {

            redirectAttributes.addFlashAttribute(
                    "erro", "E-mail ou senha inválidos."
            );

            return "redirect:/";
        }

        Usuario usuario = usuarioEncontrado.get();

        if (!"ALUNO".equals(usuario.getPerfil()) &&
                !"TECNICO".equals(usuario.getPerfil())) {

            session.invalidate();

            redirectAttributes.addFlashAttribute(
                    "erro", "Perfil de usuário inválido."
            );

            return "redirect:/";
        }

        session.invalidate();
        session = request.getSession(true);

        session.setAttribute("usuarioId", usuario.getId());
        session.setAttribute("usuarioNome", usuario.getNome());
        session.setAttribute("usuarioPerfil", usuario.getPerfil());

        boolean lembrar = "on".equals(lembrarMe) ||
                "true".equals(lembrarMe);

        String emailCodificado = lembrar
                ? Base64.getUrlEncoder().withoutPadding()
                .encodeToString(
                        email.getBytes(StandardCharsets.UTF_8))
                : "";

        ResponseCookie cookie = ResponseCookie
                .from("email_lembrado", emailCodificado)
                .httpOnly(true)
                .secure(environment.acceptsProfiles(
                        Profiles.of("prod")))
                .sameSite("Lax")
                .path("/")
                .maxAge(lembrar
                        ? Duration.ofDays(30)
                        : Duration.ZERO)
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );

        if ("ALUNO".equals(usuario.getPerfil())) {
            return "redirect:/aluno";
        }

        return "redirect:/tecnico";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }
}