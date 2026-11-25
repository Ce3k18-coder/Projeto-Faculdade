
package com.faculdade.sistemachamados.controller;

import com.faculdade.sistemachamados.model.Chamado;
import com.faculdade.sistemachamados.model.Usuario;
import com.faculdade.sistemachamados.repository.ChamadoRepository;
import com.faculdade.sistemachamados.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class PainelTecnicoController {

    private final ChamadoRepository chamadoRepository;
    private final UsuarioRepository usuarioRepository;

    public PainelTecnicoController(
            ChamadoRepository chamadoRepository,
            UsuarioRepository usuarioRepository) {

        this.chamadoRepository = chamadoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/tecnico")
    public String painelTecnico(HttpSession session, Model model) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String usuarioNome = (String) session.getAttribute("usuarioNome");
        String usuarioPerfil = (String) session.getAttribute("usuarioPerfil");

        if (usuarioId == null || !"TECNICO".equals(usuarioPerfil)) {
            return "redirect:/";
        }

        List<Chamado> chamados = chamadoRepository.findAll();

        long abertos = chamados.stream()
                .filter(c -> "ABERTO".equals(c.getStatus()))
                .count();

        long andamento = chamados.stream()
                .filter(c -> "EM_ANDAMENTO".equals(c.getStatus()))
                .count();

        long finalizados = chamados.stream()
                .filter(c -> "FINALIZADO".equals(c.getStatus()))
                .count();

        model.addAttribute("nomeTecnico", usuarioNome);
        model.addAttribute("chamados", chamados);
        model.addAttribute("totalChamados", chamados.size());
        model.addAttribute("chamadosAbertos", abertos);
        model.addAttribute("chamadosAndamento", andamento);
        model.addAttribute("chamadosFinalizados", finalizados);

        return "painel-tecnico";
    }

    @GetMapping("/tecnico/chamados/{id}")
    public String visualizarChamado(
            @PathVariable("id") Long id,
            HttpSession session,
            Model model) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String usuarioNome = (String) session.getAttribute("usuarioNome");
        String usuarioPerfil = (String) session.getAttribute("usuarioPerfil");

        if (usuarioId == null || !"TECNICO".equals(usuarioPerfil)) {
            return "redirect:/";
        }

        Chamado chamado = chamadoRepository.findById(id)
                .orElse(null);

        if (chamado == null) {
            return "redirect:/tecnico";
        }

        model.addAttribute("nomeTecnico", usuarioNome);
        model.addAttribute("chamado", chamado);

        return "detalhes-chamado";
    }

    @PostMapping("/tecnico/chamados/{id}/assumir")
    public String assumirChamado(
            @PathVariable("id") Long id,
            HttpSession session) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String usuarioPerfil = (String) session.getAttribute("usuarioPerfil");

        if (usuarioId == null || !"TECNICO".equals(usuarioPerfil)) {
            return "redirect:/";
        }

        Usuario tecnico = usuarioRepository.findById(usuarioId)
                .orElse(null);

        if (tecnico == null || !"TECNICO".equals(tecnico.getPerfil())) {
            session.invalidate();
            return "redirect:/";
        }

        Chamado chamado = chamadoRepository.findById(id)
                .orElse(null);

        if (chamado == null) {
            return "redirect:/tecnico";
        }

        if ("ABERTO".equals(chamado.getStatus())
                && chamado.getTecnico() == null) {

            chamado.setTecnico(tecnico);
            chamado.setStatus("EM_ANDAMENTO");

            chamadoRepository.save(chamado);
        }

        return "redirect:/tecnico/chamados/" + id;
    }
}
