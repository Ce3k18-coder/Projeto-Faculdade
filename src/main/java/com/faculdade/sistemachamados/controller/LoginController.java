
package com.faculdade.sistemachamados.controller;

import com.faculdade.sistemachamados.model.Usuario;
import com.faculdade.sistemachamados.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@Controller
public class LoginController {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public LoginController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String senha,
            HttpSession session,
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

        session.setAttribute("usuarioId", usuario.getId());
        session.setAttribute("usuarioNome", usuario.getNome());
        session.setAttribute("usuarioPerfil", usuario.getPerfil());

        if ("ALUNO".equals(usuario.getPerfil())) {
            return "redirect:/aluno";
        }

        if ("TECNICO".equals(usuario.getPerfil())) {
            return "redirect:/tecnico";
        }

        session.invalidate();
        redirectAttributes.addFlashAttribute(
                "erro", "Perfil de usuário inválido."
        );

        return "redirect:/";
    }
}
